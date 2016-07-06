lazy val _scalaVersion = "2.11.8"
lazy val doctusVersion = "1.0.6-SNAPSHOT"
lazy val mockitoVersion = "1.9.5"
lazy val utestVersion = "0.4.1"
lazy val scalaJsDomVersion = "0.9.1"
lazy val scalaJsJqueryVersion = "0.9.0"

lazy val commonSettings = 
  Seq(
    version := "0.1.0-SNAPSHOT",
    scalaVersion := _scalaVersion,
    organization := "net.entelijan",
    organizationHomepage := Some(url("http://entelijan.net/")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    EclipseKeys.withSource := true)

lazy val coreSettings =
  commonSettings ++
    Seq(
      libraryDependencies += "net.entelijan" %%% "doctus-core" % doctusVersion,
      libraryDependencies += "com.lihaoyi" %%% "utest" % utestVersion % "test",
      testFrameworks += new TestFramework("utest.runner.Framework"))

lazy val jvmSettings =
  commonSettings ++
    Seq(
      libraryDependencies += "net.entelijan" %% "doctus-jvm" % doctusVersion,
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      fork := true,
      testFrameworks += new TestFramework("utest.runner.Framework"))

lazy val scalajsSettings =
  commonSettings ++
    Seq(
      jsDependencies += RuntimeDOM,
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion,
      libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % scalaJsJqueryVersion,
      libraryDependencies += "net.entelijan" %%% "doctus-scalajs" % doctusVersion,
      testFrameworks += new TestFramework("utest.runner.Framework"))

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "doctuswebaudiotryout-root")
  .aggregate(
    core,
    jvm,
    scalajs)

lazy val core = (project in file("doctuswebaudiotryout-core"))
  .settings(coreSettings: _*)
  .settings(
    name := "doctuswebaudiotryout-core")
  .enablePlugins(ScalaJSPlugin)

lazy val jvm = (project in file("doctuswebaudiotryout-jvm"))
  .settings(jvmSettings: _*)
  .settings(
    name := "doctuswebaudiotryout-jvm")
  .dependsOn(core)

lazy val scalajs = (project in file("doctuswebaudiotryout-scalajs"))
  .settings(scalajsSettings: _*)
  .settings(
    name := "doctuswebaudiotryout-scalajs")
  .dependsOn(core)
  .enablePlugins(ScalaJSPlugin)

