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

import java.util.Properties

import br.ufrj.gta.kafka.adapter.Protocol.KafkaAdapterConfig
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.concurrent.{ ExecutionContext, Future }

object KafkaUtils extends LazyLogging {
  def checkTopicsExistence(config: KafkaAdapterConfig)(
      implicit ec: ExecutionContext
  ): Future[Unit] =
    Future {
      logger.debug(s"Looking for the existence of topics ${config.adapter.topicsIn}")
      val props = new Properties()
      props.put("bootstrap.servers", s"${config.kafka.host}:${config.kafka.port}")
      props.put("group.id", s"${config.adapter.clientId}")
      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
      props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
      val consumer      = new KafkaConsumer[String, String](props)
      val currentTopics = consumer.listTopics().keySet()
      //
      consumer.close()
      currentTopics
    }.flatMap { ct =>
      config.adapter.topicsIn.collect {
        case present if !ct.contains(present) =>
          present
      } match {
        case l if l.isEmpty =>
          Future.unit
        case missing =>
          Future.failed(Error.ConfigError(missing.mkString("Missing topics: ", ",", ".")))
      }
    }

}
