import scala.util.Properties

val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.0.1-SNAPSHOT",
  organization := "org.scala-js",
  scalaVersion := "2.12.0-SNAPSHOT",
  scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),

  homepage := Some(url("http://scala-js.org/")),
  licenses += ("BSD New",
      url("https://github.com/scala-js/scala-2.12-junit-mixin-plugin/blob/master/LICENSE")),
  scmInfo := Some(ScmInfo(
      url("https://github.com/scala-js/scala-2.12-junit-mixin-plugin"),
      "scm:git:git@github.com:scala-js/scala-2.12-junit-mixin-plugin.git",
      Some("scm:git:git@github.com:scala-js/scala-2.12-junit-mixin-plugin.git")))
)

val isGeneratingEclipse =
  Properties.envOrElse("GENERATING_ECLIPSE", "false").toBoolean

lazy val jUnitMixinPlugin = Project(
  id = "jUnitMixinPlugin",
  base = file("junit-mixin-plugin"),
  settings = commonSettings ++ Seq(
    name := "JUnit mixin support Scala for Scala 2.12.",
    crossVersion := CrossVersion.full,
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    exportJars := true
  )
)

lazy val testSuite = Project(
  id = "jUnitMixinPluginTest",
  base = file("junit-mixin-plugin-test"),
  settings = commonSettings ++ Seq(
    name := "Tests for JUnit mixin support Scala for Scala 2.12.",
    libraryDependencies +=
      "com.novocode" % "junit-interface" % "0.9" % "test",
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a"),
    scalacOptions in Test ++= {
      if (isGeneratingEclipse) {
        Seq.empty
      } else {
        val jar = (packageBin in (jUnitMixinPlugin, Compile)).value
        Seq(s"-Xplugin:$jar")
      }
    }
  )
)
