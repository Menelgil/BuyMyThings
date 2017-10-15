scalaVersion := "2.12.3"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")


lazy val commonSettings = Seq(
  organization := "com.radix",
  version := "1.0",
  scalaVersion := "2.12.3"
)

lazy val UI = (project in file("ui"))
  .settings(
    commonSettings,
    name := "ui"
  ).enablePlugins(PlayScala)

lazy val root = (project in file(".")).aggregate(
  UI
)