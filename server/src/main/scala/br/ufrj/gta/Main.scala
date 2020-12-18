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
import br.ufrj.gta.api.{ SamplerService }
import zio._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze._
import zio._
import zio.console.Console
import zio.interop.catz._
import zio.interop.catz.implicits._
import org.http4s.server.middleware._

import scala.concurrent.ExecutionContext.global
object Main extends App {

  val server: ZIO[ZEnv, Throwable, Unit] = ZIO
    .runtime[ZEnv]
    .flatMap { implicit rts =>
      BlazeServerBuilder[Task](global)
        .bindHttp(7777, "localhost")
        .withHttpApp(CORS(SamplerService.service))
        .serve
        .compile
        .drain
    }

  def run(args: List[String]): URIO[ZEnv with Console, ExitCode] =
    server.exitCode
}
