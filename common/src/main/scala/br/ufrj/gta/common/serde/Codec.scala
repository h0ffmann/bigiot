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

///*
// * Copyright 2020 Matheus Hoffmann
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package br.ufrj.gta.common.serde
//
//import br.ufrj.gta.common.model.sensor.SensorRegistry
//import kantan.csv.RowEncoder
//
//trait Codec {
//  implicit val srEncoder: RowEncoder[SensorRegistry] =
//    RowEncoder.encoder(0, 2, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11) { (sr: SensorRegistry) =>
//      (
//        sr.timestamp,
//        sr.device.id,
//        sr.device.ver,
//        sr.device.ramUsage,
//        sr.device.diskUsage,
//        sr.device.cpuUsage,
//        sr.sensor.sensorType,
//        sr.sensor.amount,
//        sr.sensor.varAmount,
//        sr.sensor.minAmount,
//        sr.sensor.maxAmount,
//        sr.sensor.status.toString()
//      )
//    }
//}
