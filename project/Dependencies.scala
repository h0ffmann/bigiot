import sbt._

object Dependencies {

  object V {
    val akka     = "2.6.10"
    val akkaHttp = "10.2.1"
    val log4j    = "2.13.3"
    val kantan   = "0.6.1"
  }

  object Lib {
    val alpakkaMqtt  = "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "2.0.2"
    val alpakkaKafka = "com.typesafe.akka"  %% "akka-stream-kafka"        % "2.0.5"
    val akkaStream   = "com.typesafe.akka"  %% "akka-stream"              % V.akka
    val akkaTyped    = "com.typesafe.akka"  %% "akka-actor-typed"         % V.akka
    val akkaSlf4j    = "com.typesafe.akka"  %% "akka-slf4j"               % V.akka

    val logback      = "ch.qos.logback"             % "logback-classic" % "1.2.3"
    val janino       = "org.codehaus.janino"        % "janino"          % "3.1.2"
    val slf4j        = "org.slf4j"                  % "slf4j-api"       % "1.7.30"
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2"

    val protoBuffer = "com.thesamet.scalapb" %% "scalapb-runtime"    % scalapb.compiler.Version.scalapbVersion % "protobuf"
    val json4sProto = "com.thesamet.scalapb" %% "scalapb-json4s"     % "0.10.1"
    val csv         = "com.nrinaudo"         %% "kantan.csv"         % V.kantan
    val csvGeneric  = "com.nrinaudo"         %% "kantan.csv-generic" % V.kantan

    val rainier  = "com.stripe"     %% "rainier-core"  % "0.3.0"
    val evilPlot = "com.cibo"       %% "evilplot-repl" % "0.8.0"
    val gen      = "org.scalacheck" %% "scalacheck"    % "1.14.1"

    val munit        = "org.scalameta"    %% "munit"                    % "0.7.13" % Test
    val kafkas4sTest = "org.apache.kafka" %% "kafka-streams-test-utils" % "2.6.0"  % Test
    val scalaTest    = "org.scalatest"    %% "scalatest"                % "3.2.2"  % IntegrationTest

    val avroSer  = "io.confluent"        % "kafka-avro-serializer" % "6.0.0"
    val kafkas4s = "org.apache.kafka"    %% "kafka-streams-scala"  % "2.6.0"
    val avro4s   = "com.sksamuel.avro4s" %% "avro4s-core"          % "4.0.0"

    val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.14.0"

    val ConfigBundle = Seq(pureConfig)
    //val KafkaBundle = Seq(avroSer, avro4s, kafkas4s)
    val TestBundle  = Seq(munit, scalaTest) //, kafkas4sTest)
    val StatsBundle = Seq(gen) //, rainier, evilPlot)
    val SerBundle   = Seq(protoBuffer, csv, csvGeneric, json4sProto)
    val AkkaBundle  = Seq(akkaTyped, akkaStream, alpakkaMqtt, akkaSlf4j, alpakkaKafka)
    val LogBundle   = Seq(logback, slf4j, scalaLogging)
  }

}
