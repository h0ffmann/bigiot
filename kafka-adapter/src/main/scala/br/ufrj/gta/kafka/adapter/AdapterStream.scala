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

import akka.kafka.ProducerSettings
import akka.stream.scaladsl.Source
import akka.{ Done, NotUsed }
import br.ufrj.gta.kafka.adapter.Protocol.KafkaAdapterConfig

import scala.concurrent.ExecutionContext

object AdapterStream {

  def create(cfg: KafkaAdapterConfig, producerConfig: ProducerSettings[String, String])(
      implicit ec: ExecutionContext
  ): Source[Done, NotUsed] =
    MQTTReactiveSource(cfg)
      .via(KafkaFlow(producerConfig))
      .mapAsync(parallelism = cfg.adapter.parallelism)(x => x.ack())
}
