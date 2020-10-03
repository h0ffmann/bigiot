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

package mqttproxy

import akka.Done
import akka.actor.{ ActorSystem, CoordinatedShutdown }
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.alpakka.mqtt.{ MqttMessage, MqttQoS }
import akka.stream.scaladsl.{ Keep, Sink, Source }
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ ExecutionContext, Future }

object MainMqttProxy extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val id = scala.util.Properties.envOrElse("CLIENT_ID", "mqtt-proxy")

    implicit val sys: ActorSystem     = ActorSystem(id)
    implicit val ec: ExecutionContext = sys.dispatcher

    (for {
      cfg <- MqttProxyConfigLoader(id)
      _   <- KafkaUtils.checkTopicsExistence(cfg, cfg.topicsIn ++ cfg.topicsOut: _*)
      producerCfg = KafkaFlow.createProducerSettings(cfg)

      _ = MQTTReactiveSource(cfg)
        .via(KafkaFlow(producerCfg))
        .mapAsync(parallelism = 1)(x => x.ack())
        .toMat(Sink.seq)(Keep.left)
        .run()

      //test
      cS       = MQTTReactiveSource.createConnectionSettings(s"tcp://${cfg.mqttHost}:${cfg.mqttPort}", "test-publisher")
      testSink = MqttSink(cS, MqttQoS.AtLeastOnce)
      _ = Source(
        List.fill(100)(())
      ).map(_ => createFakeMessage(cfg.topicsIn.head))
        .throttle(1, 1.second)
        .runWith(testSink)
    } yield ())
      .recover {
        case t: Throwable =>
          sys.log.error(Console.RED + t.getMessage + Console.RESET)
          CoordinatedShutdown(sys)
            .run(
              CoordinatedShutdown.incompatibleConfigurationDetectedReason
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

  def createFakeMessage(topic: String): MqttMessage =
    MqttMessage(topic, ByteString(s"${System.currentTimeMillis()}"))
      .withQos(MqttQoS.AtLeastOnce)
      .withRetained(true)

}
