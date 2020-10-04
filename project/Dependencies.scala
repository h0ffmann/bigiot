import sbt._

object Dependencies {

  object Version {
    val akka  = "2.6.8"
    val log4j = "2.13.3"
  }

  object Lib {
    //val alpakkaMqtt = "com.lightbend.akka" %% "akka-stream-alpakka-mqtt-streaming" % "2.0.2"
    val alpakkaMqtt  = "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "2.0.2"
    val alpakkaKafka = "com.typesafe.akka"  %% "akka-stream-kafka"        % "2.0.5"
    val akkaStream   = "com.typesafe.akka"  %% "akka-stream"              % Version.akka
    val akkaTyped    = "com.typesafe.akka"  %% "akka-actor-typed"         % Version.akka
    val akkaSlf4j    = "com.typesafe.akka"  %% "akka-slf4j"               % Version.akka

    val logback      = "ch.qos.logback"             % "logback-classic" % "1.2.3"
    val janino       = "org.codehaus.janino"        % "janino"          % "3.1.2"
    val slf4j        = "org.slf4j"                  % "slf4j-api"       % "1.7.30"
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2"

    val protoBuffer   = "com.thesamet.scalapb" %% "scalapb-runtime"    % scalapb.compiler.Version.scalapbVersion % "protobuf"
    val json4sProto   = "com.thesamet.scalapb" %% "scalapb-json4s"     % "0.10.1"
    val csv           = "com.nrinaudo"         %% "kantan.csv"         % "0.6.1"
    val csvGeneric    = "com.nrinaudo"         %% "kantan.csv-generic" % "0.6.1"
    val apacheCommons = "commons-codec"        % "commons-codec"       % "1.15"

    //    val rainier  = "com.stripe" %% "rainier-core"  % "0.3.0+18-05e6d208+20200211-2044"
//    val evilPlot = "com.cibo"   %% "evilplot-repl" % "0.6.3"
    val gen = "org.scalacheck" %% "scalacheck" % "1.14.1" //% "test"

    val munit = "org.scalameta" %% "munit" % "0.7.13" % Test

    val TestBundle  = Seq(munit)
    val StatsBundle = Seq(gen)
    val SerBundle   = Seq(protoBuffer, csv, csvGeneric, json4sProto, apacheCommons)
    val AkkaBundle  = Seq(akkaTyped, akkaStream, alpakkaMqtt, akkaSlf4j, alpakkaKafka)
    val LogBundle   = Seq(logback, janino, slf4j, scalaLogging)
  }

}
