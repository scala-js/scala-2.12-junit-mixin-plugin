# scala-2.12-junit-mixin: restore your JUnit `@Test` methods in traits

[![Build Status](https://travis-ci.org/scala-js/scala-2.12-junit-mixin-plugin.svg?branch=master)](https://travis-ci.org/scala-js/scala-2.12-junit-mixin-plugin)

With Scala 2.12.0-M5 and JUnit 4, you might have encountered warnings such as the following:

    [warn] TraitToClassMixinTest.scala:24: JUnit tests in traits that are
           compiled as default methods are not executed by JUnit 4.
           JUnit 5 will fix this issue.
    [warn]   @Test def test2(): Unit = {
    [warn]             ^

Uh oh! Some of your test suite just doesn't run anymore!
As the warning tells you, this will be fixed if you upgrade to JUnit 5 ... except that is not released yet.

`scala-2.12-junit-mixin` solves this issue right now, with Scala 2.12.0-M5 and JUnit 4.
Simply add the following to your project settings:

```scala
libraryDependencies ++= {
  if (scalaVersion.value.startsWith("2.12."))
    Seq(compilerPlugin("org.scala-js" % "scala-junit-mixin-plugin" % "0.1.0" cross CrossVersion.full))
  else
    Seq.empty
}
```

`scala-2.12-junit-mixin-plugin` is platform-independent.
It works with Scala on the JVM and with Scala.js.

## License

`scala-2.12-junit-mixin-plugin` is distributed under the
[BSD 3-Clause license](./LICENSE.txt).

## Contributing

Follow the [contributing guide](./CONTRIBUTING.md).
