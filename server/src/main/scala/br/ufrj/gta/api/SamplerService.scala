/*
 * Copyright 2020 Matheus Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufrj.gta.api

import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.alpakka.mqtt.{ MqttConnectionSettings, MqttMessage, MqttQoS }
import akka.stream.scaladsl.{ Sink, Source }
import akka.util.ByteString
import br.ufrj.gta.common.gen.Generator
import br.ufrj.gta.common.model.sensor.StatusType
import br.ufrj.gta.kafka.adapter.MQTTReactiveSource
import com.typesafe.scalalogging.LazyLogging
import org.http4s.{ HttpApp, HttpRoutes }
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import scalapb.json4s.JsonFormat
import zio.Task
import zio.interop.catz._
import zio.interop.catz.implicits._
import com.softwaremill.quicklens._
import io.circe.Json

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationLong

/**
  * simplest possible service with a single get as string
  */
object SamplerService extends LazyLogging {

  implicit val sys: ActorSystem     = ActorSystem("sampler")
  implicit val ec: ExecutionContext = sys.dispatcher
  var simulationRunning: Boolean    = false

  def calculateSimulation(spr: SimulationProfileRequest): String = {
    val simulationTimeInMinutes
        : Double                               = (spr.messages / (spr.speedInMillis / 1000.0)) / (60 * spr.parallelism)
    val totalMessages: Int                     = spr.messages * spr.deviceNumber
    val approximateDownCounts: Double          = totalMessages * spr.probabilityDown
    val approximateSerializationErrors: Double = totalMessages * spr.probabilitySerError

    s"""Expected time: $simulationTimeInMinutes minutes | Total messages: $totalMessages | Approximate down count: $approximateDownCounts | Approximate serialization errors: $approximateSerializationErrors"""
  }

  def createSinks(i: Int): Sink[MqttMessage, Future[Done]] = {
    val cs = MQTTReactiveSource.createConnectionSettings(
      s"tcp://localhost:1883",
      s"sampler-$i"
    )
    MqttSink(cs, MqttQoS.AtLeastOnce)
  }

  def createStream(
      deviceId: String,
      spr: SimulationProfileRequest,
      sink: Sink[MqttMessage, Future[Done]]
  ): Future[Done] =
    Source(1 until spr.messages)
      .map(_ => Generator.sensorRegistry.sample.get)
      .throttle(spr.parallelism, spr.speedInMillis.millis)
      .map { x =>
        val isDown               = scala.math.random() < spr.probabilityDown
        val isWrongSerialization = scala.math.random() < spr.probabilitySerError
        MqttMessage(
          "sensor-in-1",
          if (isWrongSerialization) {
            ByteString("wrong message!")
          } else {
            ByteString(s"${JsonFormat.toJsonString(
              x.modify(_.device.id)
                .setTo(deviceId)
                .modify(_.sensor.status)
                .setTo(if (isDown) StatusType.DOWN else StatusType.OK)
                .modify(_.timestamp)
                .setTo(System.currentTimeMillis())
            )}")
          }
        ).withQos(MqttQoS.AtLeastOnce)
          .withRetained(true)
      }
      .map { x =>
        println(x.payload.utf8String)
        x
      }
      .runWith(sink)

  private val dsl = Http4sDsl[Task]
  import dsl._

  val service: HttpApp[Task] = HttpRoutes
    .of[Task] {

      case GET -> Root / "ping" => Ok("Pong!")

      case GET -> Root / "running" => Ok(simulationRunning)

      case request @ POST -> Root / "api" / "alert-notifications" / "test" =>
        request
          .as[Json]
          .map(
            js => println(Console.YELLOW + s"GRAFANA ALERT! \n ${js.noSpaces} \n" + Console.RESET)
          ) *>
            Ok(simulationRunning)

      case request @ POST -> Root / "load" =>
        request
          .decode[SimulationProfileRequest] { spr =>
            println(s"Received request for simulation starting ...")
            simulationRunning = true
            val msg = calculateSimulation(spr)
            logger.info(msg)
            val sinkList = (1 to spr.deviceNumber).map { n =>
              s"sampler_$n" -> createSinks(n)
            }
            Future
              .sequence(
                sinkList.map(k => createStream(k._1, spr, k._2))
              )
              .map { _ =>
                simulationRunning = false
                logger.info(
                  Console.RED + "============ SIMULATION FINISHED ==============" + Console.RESET
                )
              }
            Accepted(msg)
          }

    }
    .orNotFound

  //def printer

}
