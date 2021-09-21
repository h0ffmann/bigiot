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
//package br.ufrj.gta.common.gen
//
//import java.util.UUID
//
//import br.ufrj.gta.common.model.sensor.{ DeviceInfo, SensorInfo, SensorRegistry, StatusType }
//import org.scalacheck.Gen
//
//object Generator {
//
//  val sensorRegistry: Gen[SensorRegistry] = for {
//    _ <- Gen.const(())
//    timestamp    = System.currentTimeMillis()
//    srSensorType = "temp"
//    srSensorAmount    <- Gen.choose(-1.0f, 1.0f)
//    srSensorVarAmount <- Gen.choose(0.01f, 0.05f)
//    srSensorMaxAmount <- Gen.choose(srSensorAmount, srSensorAmount + srSensorVarAmount)
//    srSensorMinAmount <- Gen.choose(srSensorAmount - srSensorVarAmount, srSensorAmount)
//    srSensorStatus    <- Gen.oneOf[StatusType](StatusType.values)
//    srDeviceId  = UUID.fromString("fc622bae-0683-11eb-adc1-0242ac120002")
//    srDeviceVer = "0.0.1"
//    srDeviceRam  <- Gen.choose(0, 10)
//    srDeviceDisk <- Gen.choose(0, 10)
//    srDeviceCpu  <- Gen.choose(0, 10)
//  } yield {
//    SensorRegistry(
//      timestamp,
//      DeviceInfo(srDeviceId.toString, srDeviceVer, srDeviceRam, srDeviceDisk, srDeviceCpu),
//      SensorInfo(
//        srSensorType,
//        srSensorAmount,
//        srSensorVarAmount,
//        srSensorMinAmount,
//        srSensorMaxAmount,
//        srSensorStatus
//      )
//    )
//  }
//}
