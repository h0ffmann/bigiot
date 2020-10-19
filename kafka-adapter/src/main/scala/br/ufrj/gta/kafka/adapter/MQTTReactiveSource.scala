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
import akka.stream.RestartSettings
import akka.stream.alpakka.mqtt.scaladsl.{ MqttMessageWithAck, MqttSource }
import akka.stream.alpakka.mqtt.{ MqttConnectionSettings, MqttQoS, MqttSubscriptions }
import akka.stream.scaladsl.{ RestartSource, Source }
import br.ufrj.gta.kafka.adapter.Protocol.KafkaAdapterConfig
import com.typesafe.scalalogging.LazyLogging
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

object MQTTReactiveSource extends LazyLogging {

  def createConnectionSettings(urlConn: String, id: String): MqttConnectionSettings =
    MqttConnectionSettings(
      urlConn,
      id,
      new MemoryPersistence()
    )

  def apply(
      config: KafkaAdapterConfig
  )(implicit ec: ExecutionContext): Source[MqttMessageWithAck, NotUsed] = {

    val urlConnStr    = s"tcp://${config.mqtt.host}:${config.mqtt.port}"
    val subscriptions = config.adapter.topicsIn.map(t => t -> MqttQoS.atLeastOnce).toMap
    val authUser      = config.authTuple.map(_._1).getOrElse("")
    val authPassword  = config.authTuple.map(_._2).getOrElse("")

    val connectionSettings = createConnectionSettings(urlConnStr, config.adapter.clientId)
      .withAutomaticReconnect(true)
      .withAuth(authUser, authPassword)

    val recoverSettings = RestartSettings(
      minBackoff = 15.seconds,
      maxBackoff = 2.hours,
      randomFactor = 0.2
    ).withMaxRestarts(20, 5.minutes) // limits the amount of restarts to 20 within 5 minutes

    RestartSource
      .withBackoff(recoverSettings) { () =>
        MqttSource
          .atLeastOnce(
            connectionSettings
              .withClientId(clientId = config.adapter.clientId),
            MqttSubscriptions(subscriptions),
            bufferSize = 8
          )
          .mapMaterializedValue(
            f =>
              f.onComplete {
                case Failure(exception) =>
                  logger.error(
                    Console.RED + s"Failed to connect to MQTT Broker $exception" + Console.RESET
                  )

                case Success(_) =>
                  logger.info(
                    Console.GREEN + s"Successfully connected to broker $urlConnStr " + Console.RESET
                  )
                  logger.info(
                    //Console.GREEN + s"${connSettings.toString().flatMap(c => if (c == ',') s"$c\n" else c.toString)}" + Console.RESET
                    Console.GREEN + s"${connectionSettings.toString()}" + Console.RESET
                  )
              }
          )
      }
      .map { x =>
        logger.debug(Console.BOLD + s"Message from source: ${x.message}" + Console.RESET)
        x
      }
  }
}
