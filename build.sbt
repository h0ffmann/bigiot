import Dependencies._

lazy val rootProjectName       = "bigiot"
lazy val kafkaBridgeModuleName = "kafka-adapter"
lazy val commonModuleName      = "common"
lazy val serverModuleName      = "server"

lazy val scala213 = "2.13.3"

lazy val root =
  project
    .in(file("."))
    .withId(rootProjectName)
    .aggregate(kafkaBridge, common, server)
    .settings(
      crossScalaVersions := Nil,
      publish / skip := true
    )

lazy val common =
  project
    .in(file(commonModuleName))
    .withId(commonModuleName)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings: _*)
    .settings(settings)
    .settings(
      libraryDependencies ++=
          Seq(Lib.AkkaBundle, Lib.LogBundle, Lib.SerBundle, Lib.StatsBundle, Lib.TestBundle).flatten
    )
    .settings(
       Compile / PB.targets := Seq(
            scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
          )
    )
    .settings(run / fork := true)
    .enablePlugins(AssemblyPlugin, AutomateHeaderPlugin, BuildInfoPlugin, ProtocPlugin)

lazy val server =
  project
    .in(file(serverModuleName))
    .withId(serverModuleName)
    .settings(settings)
    .settings(
      libraryDependencies ++=
          Seq(Lib.AkkaBundle, Lib.LogBundle, Lib.ZioBundle, Lib.Http4sBundle).flatten
    )
    .settings(run / fork := true)
    .enablePlugins(AssemblyPlugin, AutomateHeaderPlugin, BuildInfoPlugin, ProtocPlugin)
    .dependsOn(common, kafkaBridge)

lazy val kafkaBridge =
  project
    .in(file(kafkaBridgeModuleName))
    .withId(kafkaBridgeModuleName)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings: _*)
    .settings(settings)
    .settings(
      libraryDependencies ++=
          Seq(
            Lib.AkkaBundle,
            Lib.LogBundle,
            Lib.TestBundle,
            Lib.ConfigBundle,
            Lib.MetricsBundle
          ).flatten
    )
    .settings(addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"))
    .settings(run / fork := true)
    .enablePlugins(AssemblyPlugin, AutomateHeaderPlugin, BuildInfoPlugin)
    .dependsOn(common)

lazy val settings =
  Seq(
    resolvers ++= Seq(
          Resolver.defaultLocal,
          Resolver.mavenLocal,
          Resolver.mavenCentral,
          Classpaths.typesafeReleases,
          Classpaths.sbtPluginReleases,
          Resolver.bintrayRepo("rainier", "maven"),
          Resolver.bintrayRepo("cibotech", "public"),
          "Confluent Maven Repository" at "https://packages.confluent.io/maven"
        ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Global / cancelable := true,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafmtOnCompile := true,
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
    scalaVersion := scala213,
    version := "0.0.1",
    organization := "me.mhoffmann",
    organizationName := "Matheus Hoffmann",
    startYear := Some(2020),
    licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://www.gta.ufrj.br/")),
    developers := List(
          Developer(
            id = "h0ffmann",
            name = "Matheus Hoffmann",
            email = "hoffmann [at] poli.ufrj.br",
            url = url("https://github.com/h0ffmann")
          )
        ),
    testFrameworks += new TestFramework("munit.Framework"),
    Test / parallelExecution := false,
    scalacOptions += "-Ywarn-unused"
  )

val stages = List("/compile", "/test", "/assembly", "/publishLocal")

addCommandAlias("publishAll", stages.map(s => kafkaBridgeModuleName + s).mkString(";+ ", ";+", ""))
addCommandAlias("slibs", "show libraryDependencies")
addCommandAlias("checkdeps", ";dependencyUpdates; reload plugins; dependencyUpdates; reload return")
addCommandAlias("fmt", "scalafmtAll")
addCommandAlias("prepare", "fix; fmt ; reload")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias(
  "fixCheck",
  "; compile:scalafix --check ; test:scalafix --check"
)
