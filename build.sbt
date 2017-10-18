scalaVersion := "2.12.3"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

//-----------------------
//    SETTINGS
//-----------------------

lazy val commonSettings = Seq(
  organization := "com.radix",
  version := "1.0",
  scalaVersion := "2.12.3"
)

lazy val testDependencies = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-core" % "2.11.0" % Test
)

//-----------------------
//    LIBRARIES
//-----------------------


lazy val DBTestLib = (project in file("db-test-lib"))
  .settings(
    commonSettings,
    name := "db-test-lib",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2",
      "org.reactivemongo" % "reactivemongo_2.12" % "0.12.7"
    )
  )
  .dependsOn(DBLib)

lazy val TestLib = (project in file("test-lib"))
  .settings(
    commonSettings,
    name := "test-lib",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2"
    )
  )

lazy val DBLib = (project in file("db-lib"))
  .settings(
    commonSettings,
    name := "db-lib",
    libraryDependencies ++= Seq(
      guice,
      "org.reactivemongo" % "reactivemongo_2.12" % "0.12.7"
    )
  )

//-----------------------
//    SERVICES
//-----------------------

lazy val AuthenticationService = (project in file("authentication-service"))
  .settings(
    commonSettings,
    name := "authentication-service",
    libraryDependencies ++= Seq(
      guice,
      "org.reactivemongo" % "reactivemongo_2.12" % "0.12.7",
      "org.reactivemongo" % "reactivemongo-akkastream_2.12" % "0.12.7"
    ) ++ testDependencies
  )
  .enablePlugins(PlayScala)
  .dependsOn(DBLib, TestLib % "test->compile", DBTestLib % "test->compile")

lazy val UI = (project in file("ui"))
  .settings(
    commonSettings,
    name := "ui",
    libraryDependencies ++= Seq(
      guice
    ) ++ testDependencies
  )
  .enablePlugins(PlayScala)

//-----------------------
//    ROOT
//-----------------------

lazy val root = (project in file(".")).aggregate(
  UI,
  AuthenticationService
)