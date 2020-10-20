package it

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.alpakka.mqtt.{ MqttConnectionSettings, MqttMessage, MqttQoS }
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.{ KillSwitches, UniqueKillSwitch }
import akka.stream.scaladsl.{ Keep, Sink, Source }
import akka.util.ByteString
import br.ufrj.gta.common.gen.Generator
import br.ufrj.gta.kafka.adapter.{
  AdapterStream,
  ConfigLoader,
  KafkaFlow,
  MQTTReactiveSource,
  Protocol
}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import scalapb.json4s.JsonFormat

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ Await, ExecutionContext, Future }

/**
  * Requires "docker-compose up -d zk kafka emqx"
  */
class KafkaAdapterItSpec extends AnyFlatSpec with BeforeAndAfterAll {

  val TOTAL_MESSAGES    = 100
  var deliveredMessages = 0

  implicit val sys: ActorSystem     = ActorSystem("it-test")
  implicit val ec: ExecutionContext = sys.dispatcher

  val cfg: Protocol.KafkaAdapterConfig              = Await.result(ConfigLoader(), 1.minute)
  val producerCfg: ProducerSettings[String, String] = KafkaFlow.createProducerSettings(cfg.kafka)

  val cS: MqttConnectionSettings = MQTTReactiveSource.createConnectionSettings(
    s"tcp://${cfg.mqtt.host}:${cfg.mqtt.port}",
    "test-publisher"
  )
  val testSink: Sink[MqttMessage, Future[Done]] = MqttSink(cS, MqttQoS.ExactlyOnce)
  val topicSink: String                         = cfg.adapter.topicsIn.head
  println(topicSink)

  var ks: UniqueKillSwitch = _
  override def beforeAll(): Unit = {
    println("Acquiring phase.")
    val stream =
      AdapterStream
        .create(cfg, producerCfg)
        .map { x =>
          deliveredMessages += 1; x
        }
        .viaMat(KillSwitches.single)(Keep.right)
        .toMat(Sink.last)(Keep.both)
        .run()
    ks = stream._1
  }

  override def afterAll(): Unit = {
    println("Release phase.")
    ks.shutdown()
    sys.terminate()
  }

  behavior of "Message deliver"
  it should "Deliver all messages from MQTT source to Kafka sink" in {
    val fKafkaProduced = Source(1 until TOTAL_MESSAGES)
      .map(
        _ =>
          MqttMessage(
            topicSink,
            ByteString(s"${JsonFormat.toJsonString(Generator.sensorRegistry.sample.get)}")
          ).withQos(MqttQoS.ExactlyOnce)
            .withRetained(true)
      )
      .throttle(10, 1.second)
      .runWith(testSink)
      .map { _ =>
        Thread.sleep(5000)
        deliveredMessages
      }

    val kafkaSent = Await.result(fKafkaProduced, 20.seconds)
    assert(kafkaSent == TOTAL_MESSAGES)
  }
}
