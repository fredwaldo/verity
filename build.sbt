val projectName = "verity"
val scala3version = "3.0.0-M3"
val scala2version = "2.13.4"
val verityVersion = "0.1.0"

name := projectName
version in ThisBuild := verityVersion
organization in ThisBuild := "com.ysthakur"
// scalaVersion in ThisBuild := scala3version
mainClass in (Compile, run) := Some("Main")

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = project
  .in(file("."))
  .settings(
    name := "verity",
    scalaVersion := scala3version,
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= libDeps)
  .disablePlugins()
  .aggregate(
    `verity-ast`,
    `verity-parser`,
    `verity-codegen`
  ).dependsOn(`verity-ast`, `verity-parser`)

lazy val `verity-common` = project
  .in(file("verity-common"))
  .settings(
    name := "verity-common",
    scalaVersion := scala3version,
    scalacOptions ++= commonScalacOptions
  )

lazy val `verity-ast` = project
  .in(file("verity-ast"))
  .settings(
    name := "verity-ast",
    scalaVersion := scala3version,
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= Seq(
      "org.ow2.asm" % "asm" % "8.0.1", 
      "org.ow2.asm" % "asm-util" % "8.0.1"
    )
  ).dependsOn(`verity-common`)

lazy val `verity-parser` =project
  .in(file("verity-parser"))
  .settings(
    name := "verity-parser",
    scalaVersion := scala2version,
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= Seq(
      // "org.scalatest" % "scalatest" % "3.2.2" % Test
      "org.scalatest" %% "scalatest" % "3.2.3"
    )
  ).dependsOn(`verity-ast`)

lazy val `verity-codegen` =project
  .in(file("verity-codegen"))
  .settings(
    name := "verity-codegen",
    scalaVersion := scala3version,
    libraryDependencies ++= Seq(
      "org.ow2.asm" % "asm" % "8.0.1", 
      "org.ow2.asm" % "asm-util" % "8.0.1"
    )
  )

lazy val libDeps = Seq(
  //"org.scala-lang" % "scala-reflect" % scala_version,
  //"org.scalatest"  %% "scalatest"    % "3.1.1" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

lazy val commonScalacOptions = Seq(
  "-language:implicitConversions",
//  "-explain",
  "-Yexplicit-nulls",
    "-Ycheck-init"
)
