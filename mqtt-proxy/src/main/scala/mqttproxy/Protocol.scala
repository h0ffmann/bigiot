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

package mqttproxy

object Protocol {

  val mask: String => String = str => str.map(_ => '*')

  final case class MqttProxyConfig(
      mqttHost: String,
      mqttPort: Int,
      mqttAuth: Option[(String, String)],
      clientId: String,
      kafkaHost: String,
      kafkaPort: Int,
      topicsIn: List[String],
      topicsOut: List[String]
  ) {
    override def toString: String =
      Console.YELLOW + "MQTTProxyConfig: \n" + Console.RESET +
          s"  mqttHost=$mqttHost \n" +
          s"  mqttPort=$mqttPort \n" +
          s"  mqttAuth=${mqttAuth.map(x => mask(x._1) -> mask(x._2))} \n" +
          s"  clientId=$clientId \n" +
          s"  kafkaHost=$kafkaHost \n" +
          s"  kafkaPort=$kafkaPort \n" +
          s"  topicIn=$topicsIn \n" +
          s"  topicOut=$topicsOut"

  }
}
