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

package mqttproxy

import akka.NotUsed
import akka.stream.alpakka.mqtt.{ MqttConnectionSettings, MqttQoS, MqttSubscriptions }
import akka.stream.alpakka.mqtt.scaladsl.{ MqttMessageWithAck, MqttSource }
import akka.stream.scaladsl.{ RestartSource, RestartWithBackoffSource, Source }
import com.typesafe.scalalogging.LazyLogging
import mqttproxy.Protocol.MqttProxyConfig
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

object MQTTSource extends LazyLogging {

  def apply(mqttProxyConfig: MqttProxyConfig)(implicit ec: ExecutionContext): Source[MqttMessageWithAck, NotUsed] = {

    val urlConnStr    = s"tcp://${mqttProxyConfig.mqttHost}:${mqttProxyConfig.mqttPort}"
    val subscriptions = mqttProxyConfig.topicsIn.map(t => t -> MqttQoS.atLeastOnce).toMap
    val authUser      = mqttProxyConfig.mqttAuth.map(_._1).getOrElse("")
    val authPassword  = mqttProxyConfig.mqttAuth.map(_._2).getOrElse("")

    val connSettings = MqttConnectionSettings(
      urlConnStr,
      mqttProxyConfig.clientId,
      new MemoryPersistence
    ).withAutomaticReconnect(true)
      .withAuth(authUser, authPassword)

    RestartSource.onFailuresWithBackoff(
      minBackoff = 15.seconds,
      maxBackoff = 2.hours,
      randomFactor = 0
    ) { () =>
      MqttSource
        .atLeastOnce(
          connSettings
            .withClientId(clientId = mqttProxyConfig.clientId),
          MqttSubscriptions(subscriptions),
          bufferSize = 8
        )
        .mapMaterializedValue(
          f =>
            f.onComplete {
              case Failure(exception) =>
                logger.error(Console.RED + s"Failed to connect to MQTT Broker $exception" + Console.RESET)

              case Success(_) =>
                logger.info(Console.GREEN + s"Successfully connected to broker $urlConnStr " + Console.RESET)
                logger.info(
                  //Console.GREEN + s"${connSettings.toString().flatMap(c => if (c == ',') s"$c\n" else c.toString)}" + Console.RESET
                  Console.GREEN + s"${connSettings.toString()}" + Console.RESET
                )
            }
        )
    }
  }
}
