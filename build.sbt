ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.5"

val http4sVersion = "0.23.26"
val sttpVersion = "3.8.15"

lazy val root = (project in file(".")).settings(
  name := "cats-effect-3-shopping-cart",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.12",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.12",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.12",
    // better monadic for compiler plugin as suggested by documentation
    compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
    // sttp
    "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
    "com.softwaremill.sttp.client3" %% "circe" % sttpVersion,
    // http4s
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    // circe
    "io.circe" %% "circe-generic" % "0.14.1",
    // test
    "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    "org.scalamock" %% "scalamock" % "5.2.0" % "test",
    "org.scalatestplus" %% "scalacheck-1-17" % "3.2.16.0" % "test",
    // scala fmt
    "org.scalameta" % "sbt-scalafmt_2.12_1.0" % "2.4.6"
  )
)
