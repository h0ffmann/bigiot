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

import akka.{ Done, NotUsed }
import akka.actor.{ ActorSystem, CoordinatedShutdown }
import akka.stream.OverflowStrategy
import akka.stream.alpakka.mqtt.MqttSubscriptions
import akka.stream.alpakka.mqtt.scaladsl.MqttMessageWithAck
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import mqttproxy.Protocol.MqttProxyConfig
//import akka.stream.alpakka.mqtt.streaming.scaladsl.{ ActorMqttClientSession, Mqtt }
//import akka.stream.alpakka.mqtt.streaming._
import akka.stream.scaladsl.{ Flow, Keep, Sink, Source, SourceQueueWithComplete, Tcp }
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import akka.pattern.after
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.alpakka.mqtt.{ MqttConnectionSettings, MqttMessage, MqttQoS }
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val id = scala.util.Properties.envOrElse("CLIENT_ID", "mqtt-proxy")

    implicit val sys: ActorSystem     = ActorSystem(id)
    implicit val ec: ExecutionContext = sys.dispatcher

    val configLoad = MqttProxyConfigLoader(id)
    configLoad
      .map { cfg =>
        sys.log.info(cfg.toString)
        MQTTSource(cfg)
          .map { x =>
            sys.log.debug(Console.GREEN + s" ${x.toString}" + Console.RESET)
            x
          }
          .toMat(Sink.seq)(Keep.left)
          .run()
      }
      .recover {
        case t: Throwable =>
          sys.log.error(t.getMessage)
          CoordinatedShutdown(sys)
            .run(
              CoordinatedShutdown.incompatibleConfigurationDetectedReason
            )
      }

//    println(System.getenv("LOG_LEVEL"))
//    val id: String    = "mqtt-producer"
//    val host: String  = "localhost"
//    val port: Int     = 1883
//    val topic: String = "sensor_test_in"
//

//
//    val connectionSettings = MqttConnectionSettings(
//      s"tcp://$host:$port",
//      "test-scala-client",
//      new MemoryPersistence
//    )
//
//    val sink: Sink[MqttMessage, Future[Done]] =
//      MqttSink(connectionSettings, MqttQoS.AtLeastOnce)
//
//    val a = MqttMessage(topic, ByteString("xyz"))
//      .withQos(MqttQoS.AtLeastOnce)
//      .withRetained(true)
//
//    val mqttSource: Source[MqttMessage, Future[Done]] =
//      MqttSource.atLeastOnce(
//        connectionSettings.withClientId(clientId = "listener"),
//        MqttSubscriptions(Map(topic -> MqttQoS.AtLeastOnce)),
//        bufferSize = 8
//      )
//
//    val (subscribed, streamResult) = mqttSource
//      .map { x =>
//        logger.info(Console.GREEN + s" ${x.toString()}" + Console.RESET)
//        x
//      }
//      .toMat(Sink.seq)(Keep.both)
//      .run()
//
//    Source(List.fill(10)(a))
//      .throttle(1, 1.second)
//      .map { x =>
//        logger.info(Console.BLUE + s"${x.toString()}" + Console.RESET)
//        x
//      }
//      .runWith(sink)

    CoordinatedShutdown(sys)
      .addCancellableTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "cleanup") { () =>
        Future {
          sys.log.info("===================================================================")
          Done
        }
      }
  }

}
