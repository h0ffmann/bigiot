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

import br.ufrj.gta.kafka.adapter.ConfigLoader
import munit.FunSuite

import scala.concurrent.ExecutionContextExecutor

class ConfigLoadSpec extends FunSuite {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  test("default loading") {
    ConfigLoader().map { cfg =>
      assertEquals(cfg.adapter.topicsIn, List("sensor-in-1"))
      assertEquals(cfg.mqtt.user, None)
      assertEquals(cfg.mqtt.password, None)
      assertEquals(cfg.kafka.create, true)
    }
  }
}