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

object Protocol {

  private val mask: String => String = str => str.map(_ => '*')

  final case class AdapterConfig(topicsIn: List[String], clientId: String)
  final case class MqttConfig(
      host: String,
      port: Int,
      user: Option[String],
      password: Option[String]
  )
  final case class KafkaConfig(host: String, port: Int, create: Boolean)

  final case class KafkaAdapterConfig(
      adapter: AdapterConfig,
      mqtt: MqttConfig,
      kafka: KafkaConfig
  ) {

    def authTuple: Option[(String, String)] = mqtt.user.flatMap(u => mqtt.password.map(p => u -> p))
    override def toString: String =
      Console.YELLOW + "\n KafkaAdapterConfig: \n" + Console.RESET +
          s"  mqttHost=${mqtt.host} \n" +
          s"  mqttPort=${mqtt.port} \n" +
          s"  mqttAuth=${authTuple.map(x => x._1 -> mask(x._2))} \n" +
          s"  clientId=${adapter.clientId} \n" +
          s"  kafkaHost=${kafka.host} \n" +
          s"  kafkaPort=${kafka.port} \n" +
          s"  topicIn=${adapter.topicsIn} \n" //+
    //s"  topicOut=$topicsOut"

  }
}
