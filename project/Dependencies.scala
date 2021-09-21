import sbt._

object Dependencies {

  object V {
    val akka     = "2.6.16"
    val akkaHttp = "10.2.1"
    val log4j    = "2.13.3"
    val kantan   = "0.6.1"
  }

  object Lib {
    val alpakkaMqtt  = "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.3"
    val alpakkaKafka = "com.typesafe.akka"  %% "akka-stream-kafka"        % "2.1.1"
    val akkaStream   = "com.typesafe.akka"  %% "akka-stream"              % V.akka
    val akkaTyped    = "com.typesafe.akka"  %% "akka-actor-typed"         % V.akka
    val akkaSlf4j    = "com.typesafe.akka"  %% "akka-slf4j"               % V.akka

    val prom        = "io.prometheus"     % "simpleclient"                  % "0.12.0"
    val promJVM     = "io.prometheus"     % "simpleclient_hotspot"          % "0.12.0"
    val promColl    = "io.prometheus.jmx" % "collector"                     % "0.16.1"
    val akkaMetrics = "fr.davit"          %% "akka-http-metrics-core"       % "1.6.0"
    val akkaProm    = "fr.davit"          %% "akka-http-metrics-prometheus" % "1.6.0"

    val logback      = "ch.qos.logback"             % "logback-classic" % "1.2.6"
    val janino       = "org.codehaus.janino"        % "janino"          % "3.1.6"
    val slf4j        = "org.slf4j"                  % "slf4j-api"       % "1.7.32"
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.4"

    val protoBuffer = "com.thesamet.scalapb" %% "scalapb-runtime"    % scalapb.compiler.Version.scalapbVersion % "protobuf"
    val json4sProto = "com.thesamet.scalapb" %% "scalapb-json4s"     % "0.12.0"
    val csv         = "com.nrinaudo"         %% "kantan.csv"         % V.kantan
    val csvGeneric  = "com.nrinaudo"         %% "kantan.csv-generic" % V.kantan

    val rainier  = "com.stripe"     %% "rainier-core"  % "0.3.0"
    val evilPlot = "com.cibo"       %% "evilplot-repl" % "0.8.0"
    val gen      = "org.scalacheck" %% "scalacheck"    % "1.15.4"

    val munit        = "org.scalameta"    %% "munit"                    % "0.7.27" % Test
    val kafkas4sTest = "org.apache.kafka" %% "kafka-streams-test-utils" % "2.6.0"  % Test
    val scalaTest    = "org.scalatest"    %% "scalatest"                % "3.2.9"  % IntegrationTest

    val avroSer  = "io.confluent"        % "kafka-avro-serializer" % "6.0.0"
    val kafkas4s = "org.apache.kafka"    %% "kafka-streams-scala"  % "2.8.0"
    val avro4s   = "com.sksamuel.avro4s" %% "avro4s-core"          % "4.0.11"

    val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.16.0"

    //http4s
    lazy val Http4sVersion     = "1.0.0-M6"
    lazy val http4sBlazeServer = "org.http4s" %% "http4s-blaze-server" % Http4sVersion
    lazy val http4sBlazeClient = "org.http4s" %% "http4s-blaze-client" % Http4sVersion
    lazy val http4sDsl         = "org.http4s" %% "http4s-dsl" % Http4sVersion
    lazy val http4sCirce       = "org.http4s" %% "http4s-circe" % Http4sVersion
    lazy val circeGeneric      = "io.circe" %% "circe-generic" % "0.14.1"
    lazy val quicklens         = "com.softwaremill.quicklens" %% "quicklens" % "1.7.4"

    lazy val zioVersion     = "1.0.11"
    lazy val zio            = "dev.zio" %% "zio" % zioVersion
    lazy val zioInteropCats = "dev.zio" %% "zio-interop-cats" % "3.1.1.0"

    lazy val mainArgs = "com.lihaoyi" %% "mainargs" % "0.2.1"

    val ZioBundle     = Seq(zio, zioInteropCats)
    val Http4sBundle  = Seq(http4sBlazeServer, http4sDsl, http4sCirce, circeGeneric, quicklens)
    val ConfigBundle  = Seq(pureConfig, mainArgs)
    val TestBundle    = Seq(munit, scalaTest) //, kafkas4sTest)
    val StatsBundle   = Seq(gen) //, rainier, evilPlot)
    val SerBundle     = Seq(protoBuffer, csv, csvGeneric, json4sProto)
    val AkkaBundle    = Seq(akkaTyped, akkaStream, alpakkaMqtt, akkaSlf4j, alpakkaKafka)
    val LogBundle     = Seq(logback, slf4j, scalaLogging)
    val MetricsBundle = Seq(prom, promJVM, promColl, akkaMetrics, akkaProm)

  }

}
