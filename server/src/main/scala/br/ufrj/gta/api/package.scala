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

package br.ufrj.gta

import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto._
import org.http4s.{ EntityDecoder, EntityEncoder }
import org.http4s.circe.{ jsonEncoderOf, jsonOf }
import zio.Task
import zio.interop.catz._

package object api {

  final case class SimulationProfileRequest(
      deviceNumber: Int,
      parallelism: Int,
      messages: Int,
      speedInMillis: Long,
      probabilityDown: Double,
      probabilitySerError: Double
  )

  implicit val encoder: Encoder[SimulationProfileRequest] = deriveEncoder
  implicit val decoder: Decoder[SimulationProfileRequest] = deriveDecoder

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[Task, A] =
    jsonOf[Task, A]

  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[Task, A] =
    jsonEncoderOf[Task, A]

}
