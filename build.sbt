import Dependencies._

ThisBuild / scalafixScalaBinaryVersion := "2.13"
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.0"

lazy val rootProjectName = "bigiot"
lazy val mqttProxyName   = "mqtt-proxy"

lazy val scala213 = "2.13.3"

lazy val root =
  project
    .in(file("."))
    .withId(rootProjectName)
    .aggregate(mqttProxy)
    .settings(
      crossScalaVersions := Nil,
      publish / skip := true
    )

lazy val mqttProxy =
  project
    .in(file(mqttProxyName))
    .withId(mqttProxyName)
    .settings(settings)
    .settings(
      libraryDependencies ++=
          (Lib.AkkaBundle ++ Lib.LogBundle)
    )
    .settings(fork in run := true)
    .enablePlugins(AssemblyPlugin, AutomateHeaderPlugin, BuildInfoPlugin)
    .disablePlugins(TpolecatPlugin)

lazy val settings =
  Seq(
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    cancelable in Global := true,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafmtOnCompile := true,
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
    scalaVersion := scala213,
    version := "0.0.1",
    organization := "me.mhoffmann",
    organizationName := "Matheus Hoffmann",
    startYear := Some(2020),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://me.hoffmann")),
    developers := List(
          Developer(
            id = "h0ffmann",
            name = "Matheus Hoffmann",
            email = "hoffmann [at] poli.ufrj.br",
            url = url("https://github.com/h0ffmann")
          )
        ),
    parallelExecution in Test := false,
    scalacOptions += "-Ywarn-unused"
  )

val stages = List("/compile", "/test", "/assembly", "/publishLocal")

addCommandAlias("publishAll", stages.map(s => mqttProxyName + s).mkString(";+ ", ";+", ""))
addCommandAlias("slibs", "show libraryDependencies")
addCommandAlias("checkdeps", ";dependencyUpdates; reload plugins; dependencyUpdates; reload return")
addCommandAlias("fmt", "scalafmtAll")
addCommandAlias("prepare", "fix; fmt ; reload")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias(
  "fixCheck",
  "; compile:scalafix --check ; test:scalafix --check"
)

resolvers in ThisBuild ++= Seq(
  Resolver.defaultLocal,
  Resolver.mavenLocal,
  Resolver.mavenCentral,
  Classpaths.typesafeReleases,
  Classpaths.sbtPluginReleases
)
