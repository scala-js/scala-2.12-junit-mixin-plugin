import scala.util.Properties

val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.1.1-SNAPSHOT",
  organization := "org.scala-js",
  scalaVersion := "2.12.0-M5",
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

lazy val `scala-junit-mixin-plugin` = project.in(file("junit-mixin-plugin")).
  settings(commonSettings).
  settings(
    crossVersion := CrossVersion.full,
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    exportJars := true,

    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
        <developers>
          <developer>
            <id>nicolasstucki</id>
            <name>Nicolas Stucki</name>
            <url>https://github.com/nicolasstucki/</url>
          </developer>
          <developer>
            <id>sjrd</id>
            <name>Sébastien Doeraene</name>
            <url>https://github.com/sjrd/</url>
          </developer>
          <developer>
            <id>gzm0</id>
            <name>Tobias Schlatter</name>
            <url>https://github.com/gzm0/</url>
          </developer>
        </developers>
    ),
    pomIncludeRepository := { _ => false }
  )

lazy val testSuite = project.in(file("junit-mixin-plugin-test")).
  settings(commonSettings).
  settings(
    libraryDependencies +=
      "com.novocode" % "junit-interface" % "0.9" % "test",
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a"),
    scalacOptions in Test ++= {
      if (isGeneratingEclipse) {
        Seq.empty
      } else {
        val jar = (packageBin in (`scala-junit-mixin-plugin`, Compile)).value
        Seq(s"-Xplugin:$jar")
      }
    }
  )
