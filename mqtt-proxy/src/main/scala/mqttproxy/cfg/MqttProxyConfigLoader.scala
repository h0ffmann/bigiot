/*
 * Copyright 2020 Hoffmann
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

package mqttproxy.cfg

import mqttproxy.Protocol.MqttProxyConfig

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

object MqttProxyConfigLoader {
  def apply(id: String)(implicit ec: ExecutionContext): Future[MqttProxyConfig] =
    Future {

      val mqttHost: String = scala.util.Properties.envOrElse("MQTT_HOST", "localhost")
      val mqttPort: Int    = scala.util.Properties.envOrElse("MQTT_PORT", "1883").toInt

      val mqttUser: Option[String] = scala.util.Properties.envOrNone("MQTT_USER")
      val mqttPass: Option[String] = scala.util.Properties.envOrNone("MQTT_PASS")

      val kafkaHost: String = scala.util.Properties.envOrElse("KAFKA_HOST", "localhost")
      val kafkaPort: Int    = scala.util.Properties.envOrElse("KAFKA_PORT", "9092").toInt

      val topicInOpt: Option[List[String]] = scala.util.Properties
        .envOrNone("TOPICS_IN")
        .map(_.split(",").toList)
      val topicOutOpt: Option[List[String]] = scala.util.Properties
        .envOrNone("TOPICS_OUT")
        .map(_.split(",").toList)

      for {
        _ <- if (mqttUser.isEmpty ^ mqttPass.isEmpty) {
              Future.failed(new IllegalArgumentException("mqtt auth must be empty/defined for user/pass"))
            } else {
              Future.unit
            }
        _ <- if (topicInOpt.isEmpty && topicOutOpt.isEmpty) {
              Future.failed(new IllegalArgumentException("topics in/out must be defined"))
            } else {
              Future.unit
            }
      } yield {
        MqttProxyConfig(
          mqttHost = mqttHost,
          mqttPort = mqttPort,
          mqttAuth = mqttUser.flatMap(u => mqttPass.map(p => u -> p)),
          clientId = id,
          kafkaHost = kafkaHost,
          kafkaPort = kafkaPort,
          topicsIn = topicInOpt.getOrElse(List.empty[String]),
          topicsOut = topicOutOpt.getOrElse(List.empty[String])
        )
      }
    }.flatten
}
