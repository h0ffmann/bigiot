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

import java.util.UUID

import akka.Done
import akka.actor.{ ActorSystem, CoordinatedShutdown }
import akka.stream.scaladsl.{ Keep, Sink }
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ ExecutionContext, Future }

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {

    implicit val sys: ActorSystem     = ActorSystem(s"kafka-adapter-${UUID.randomUUID()}")
    implicit val ec: ExecutionContext = sys.dispatcher

    (for {
      cfg <- ConfigLoader()

      _ <- if (cfg.kafka.create) {
            Future.unit
          } else {
            KafkaUtils.checkTopicsExistence(cfg)
          }

      producerCfg = KafkaFlow.createProducerSettings(cfg.kafka)

      _ = AdapterStream
        .create(cfg, producerCfg)
        .toMat(Sink.seq)(Keep.left)
        .run()

      _ = logger.info("Kafka adapter stream initialized.")

    } yield ())
      .recover {
        case t: Throwable =>
          sys.log.error(Console.RED + t.getMessage + Console.RESET)
          CoordinatedShutdown(sys)
            .run(
              CoordinatedShutdown.unknownReason
            )
      }

    CoordinatedShutdown(sys)
      .addCancellableTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "cleanup") { () =>
        Future {
          sys.log.info("===================================================================")
          Done
        }
      }
  }

}
