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

import br.ufrj.gta.common.gen.Generator
import br.ufrj.gta.kafka.adapter.KafkaFlow
import munit.FunSuite
import scalapb.json4s.JsonFormat

class ParseSpec extends FunSuite {

  test("error in parsing") {
    val fakeMsg   = "abcd"
    val fakeTopic = "topic1"
    val result    = KafkaFlow.parser(fakeMsg, fakeTopic)
    assert(result.isLeft)
    assertEquals(result.left.get._2, fakeTopic + "-error")
    assertEquals(result.left.get._1.message, fakeMsg)
    assertEquals(result.left.get._1.topic, fakeTopic)
  }

  test("correct parsing") {
    val originMessage = Generator.sensorRegistry.sample.get
    val fakeMsg       = JsonFormat.toJsonString(originMessage)
    val fakeTopic     = "topic2"
    val result        = KafkaFlow.parser(fakeMsg, fakeTopic)
    assert(result.isRight)
    result.map(r => assertEquals(r._1, originMessage))
    result.map(r => assertEquals(r._2, fakeTopic))
  }

}
