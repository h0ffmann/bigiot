package it

import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.mqtt.{ MqttConnectionSettings, MqttMessage, MqttQoS }
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.scaladsl.{ Sink, Source }
import akka.util.ByteString
import br.ufrj.gta.common.gen.Generator
import br.ufrj.gta.kafka.adapter.MQTTReactiveSource
import munit.FunSuite

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt
import scalapb.json4s.JsonFormat

/**
  * after docker-compose up emqx kafka
  */
class ProxyTest extends FunSuite {
  implicit val sys: ActorSystem     = ActorSystem("ProxyTest")
  implicit val ec: ExecutionContext = sys.dispatcher
  val TestTopic                     = "potato"
  val MessagesToSend                = 2000
  val mqttQoS: MqttQoS              = MqttQoS.AtLeastOnce
  val connectionSettings: MqttConnectionSettings = MQTTReactiveSource.createConnectionSettings(
    s"tcp://localhost:1883",
    "test-publisher"
  )
  val mqttSink: Sink[MqttMessage, Future[Done]] = MqttSink(connectionSettings, mqttQoS)

  def createFakeMessage(topic: String): MqttMessage = {
    val msg = JsonFormat.toJsonString(Generator.sensorRegistry.sample.get)
    MqttMessage(topic, ByteString(msg))
      .withQos(MqttQoS.AtLeastOnce)
      .withRetained(true)
  }

  val stream: Future[Done] = Source(
    List.fill(MessagesToSend)(())
  ).map(_ => createFakeMessage(TestTopic))
    .throttle(100, 500.millis)
    .map { x =>
      println(s"Sending test message ${x.topic} - ${x.payload}")
      x
    }
    .runWith(mqttSink)

  // TODO: Add adapter
  val proxy = null
  test("adapter tests") {
    stream.map { x =>
      Thread.sleep(1000)
      x
    }
  }
}
