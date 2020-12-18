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

package br.ufrj.gta.kafka.adapter

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ ProducerMessage, ProducerSettings }
import akka.stream.alpakka.mqtt.scaladsl.MqttMessageWithAck
import akka.stream.scaladsl.Flow
import br.ufrj.gta.common.model.sensor.{ SensorRegistry, StatusType }
import br.ufrj.gta.kafka.adapter.Error.ParseError
import br.ufrj.gta.kafka.adapter.Protocol.KafkaConfig
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import scalapb.json4s.JsonFormat

import scala.util.{ Failure, Success, Try }

object KafkaFlow extends LazyLogging {

  def parser(in: String, topic: String): Either[(ParseError, String), (SensorRegistry, String)] =
    Try(JsonFormat.fromJsonString[SensorRegistry](in)) match {
      case Success(value) => Right(value, topic)
      case Failure(exception) =>
        Left(
          ParseError(in, exception.getMessage.take(100), topic, System.currentTimeMillis()),
          topic + "-error"
        )
    }

  def createProducerSettings(
      kafkaConfig: KafkaConfig
  )(implicit sys: ActorSystem): ProducerSettings[String, String] = {
    val config: Config = sys.settings.config.getConfig("akka.kafka.producer")
    ProducerSettings[String, String](config, new StringSerializer, new StringSerializer)
      .withBootstrapServers(s"${kafkaConfig.host}:${kafkaConfig.port}")
  }

  def apply(
      producerSettings: ProducerSettings[String, String]
  ): Flow[MqttMessageWithAck, MqttMessageWithAck, NotUsed] =
    Flow[MqttMessageWithAck]
      .map { msg =>
        val (finalMessage, topic) = parser(msg.message.payload.utf8String, msg.message.topic)
          .fold(
            { l =>
              Metrics.parserError.inc()
              l._1.toString -> l._2
            }, { r =>
              val thisLabel = s"${r._1.device.id}"
              Metrics.messageCount.labels(thisLabel).inc()
              Metrics.measurementSensor.labels(thisLabel).set(r._1.sensor.amount)
              Metrics.deviceCpuUsage.labels(thisLabel).set(r._1.device.cpuUsage)
              Metrics.deviceRamUsage.labels(thisLabel).set(r._1.device.ramUsage)
              if (r._1.sensor.status == StatusType.DOWN) {
                Metrics.unavailableSensor.labels(thisLabel).inc()
              }
              //TODO fix
              val res = JsonFormat.toJsonString(r._1) -> r._2
              Metrics.timeLag.labels(thisLabel).set(System.currentTimeMillis() - r._1.timestamp)
              res
            }
          )
        val newRecord = ProducerMessage.single(
          new ProducerRecord[String, String](
            topic,
            finalMessage
          ),
          msg
        )
        newRecord
      }
      .via(Producer.flexiFlow[String, String, MqttMessageWithAck](producerSettings))
      .map { x =>
        x.passThrough
      }
      .map { x =>
        logger.info(
          Console.MAGENTA + s"Message republished: ${x.message.payload.utf8String}" + Console.RESET
        )
        x
      }
  0
}
