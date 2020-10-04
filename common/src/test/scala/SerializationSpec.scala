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
import br.ufrj.gta.common.serde.Codec

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
import kantan.csv.ops._
import scalapb.json4s.JsonFormat
class SerializationSpec extends munit.FunSuite with Codec {

  //    val bos = new BufferedOutputStream(new FileOutputStream("bin_test"))
  //    bos.write(sampleRegistry.toByteArray)
  //    bos.close()

  test("serialization specs") {
    val sampleRegistry = Generator.sensorRegistry.sample.get
    val csvRegistry    = sampleRegistry.asCsvRow.mkString(",")
    val jsonRegistry   = JsonFormat.toJsonString(sampleRegistry)
    val rawRegistry    = sampleRegistry.toByteArray
    println("=== SERIALIZATION COMPARISON ===")
    println(s"=== CSV ${csvRegistry.length}B: $csvRegistry")
    println(s"=== JSON ${jsonRegistry.length}B: $jsonRegistry")
    println(s"=== RAW ${rawRegistry.length}B: ${rawRegistry.mkString("Array(", ", ", ")")}")
    println("================================")

    assert(jsonRegistry.length > csvRegistry.length)
    assert(csvRegistry.length > rawRegistry.length)
  }

  //TODO add relative diff
  test("relative diff") {}
}
