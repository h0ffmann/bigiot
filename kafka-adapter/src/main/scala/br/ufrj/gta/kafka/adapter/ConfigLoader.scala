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

import br.ufrj.gta.kafka.adapter.Error.ConfigError
import br.ufrj.gta.kafka.adapter.Protocol.KafkaAdapterConfig
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import scala.concurrent.{ ExecutionContext, Future }

object ConfigLoader {

  def apply()(implicit ec: ExecutionContext): Future[KafkaAdapterConfig] =
    Future {
      val config = ConfigSource.default.loadOrThrow[KafkaAdapterConfig]
      for {
        _ <- if (config.mqtt.user.isEmpty ^ config.mqtt.password.isEmpty) {
              Future.failed(ConfigError("mqtt auth must be empty/defined for user/pass"))
            } else {
              Future.unit
            }
        _ <- if (config.adapter.topicsIn.isEmpty) {
              Future.failed(ConfigError("topics in/out must be defined"))
            } else {
              Future.unit
            }
      } yield config
    }.recoverWith {
      case t: Error.ConfigError =>
        Future.failed(t)
      case t: Throwable =>
        Future.failed(Error.ConfigError(t.getMessage))
    }.flatten
}
