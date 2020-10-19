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
import br.ufrj.gta.kafka.adapter.Protocol.KafkaConfig
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

object KafkaFlow extends LazyLogging {

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
        ProducerMessage.single(
          new ProducerRecord[String, String](
            msg.message.topic,
            msg.message.payload.toString()
          ),
          msg
        )
      }
      .via(Producer.flexiFlow[String, String, MqttMessageWithAck](producerSettings))
      .map(x => x.passThrough)
      .map { x =>
        logger.debug(
          Console.MAGENTA + s"Message republished: ${x.message.payload.utf8String}" + Console.RESET
        )
        x
      }

}
