name := """challenge-backend"""
organization := "com.bankin"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  ws
)

libraryDependencies += "org.assertj" % "assertj-core" % "3.13.2" % Test

