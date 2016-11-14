name := """app-wish"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "react" % "0.14.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.4.12",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.7",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
  "com.hunorkovacs" %% "koauth" % "1.1.0",
  "org.json4s" %% "json4s-native" % "3.3.0"
)

