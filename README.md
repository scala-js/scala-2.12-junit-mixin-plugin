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
/* The `test-plugin` configuration adds a plugin only to the `test`
 * configuration. It is a refinement of the `plugin` configuration which adds
 * it to both `compile` and `test`.
 */
ivyConfigurations += config("test-plugin").hide
scalacOptions in Test ++= {
  val report = update.value
  val jars = report.select(configurationFilter("test-plugin"))
  for {
    jar <- jars
    jarPath = jar.getPath
    // This is a hack to filter out the dependencies of the plugins
    if jarPath.contains("plugin")
  } yield {
    s"-Xplugin:$jarPath"
  }
}

// Add the scala-junit-mixin-plugin to the test configuration when on 2.12
libraryDependencies ++= {
  if (scalaVersion.value.startsWith("2.10.") || scalaVersion.value.startsWith("2.11."))
    Seq.empty
  else
    Seq("org.scala-js" % "scala-junit-mixin-plugin" % "0.1.0" % "test-plugin" cross CrossVersion.full)
}
```

`scala-2.12-junit-mixin-plugin` is platform-independent.
It works with Scala on the JVM and with Scala.js.

## How it works

The compiler plugin performs a very simple job.
When a JUnit-related method is inherited by a class from an interface as a
default method, an explicit forwarder is added in the class to call the default
method.

For example, consider the following Scala code:

```scala
trait MyTestsBase {
  @Test def add(): Unit =
    assertEquals(3, 1 + 2)

  def notATest(): Unit =
    assertEquals(5, 2 + 3)
}

class MyTests extends MyTestsBase
```

Scala 2.12.0-M5 identifies that `add()` does not need a forward in `MyTests`,
because it will be inherited as a default method on the JVM.
But this fools JUnit 4, which does not look for `@Test` methods in interfaces.
In this case, the plugin rewrites `MyTests` as follows:

```scala
class MyTests extends MyTestsBase {
  @Test override def add(): Unit =
    super[MyTestsBase].add()
}
```

This allows JUnit to find `MyTests.add()`.
Note that `notATest()` does not receive the plugin treatment, because it is not
JUnit-related.

JUnit-related methods are those annotated with one of the following annotations:
`@Test`, `@Before`, `@After`, `@BeforeClass`, `@AfterClass`, `@Ignore`.

## License

`scala-2.12-junit-mixin-plugin` is distributed under the
[BSD 3-Clause license](./LICENSE.txt).

## Contributing

Follow the [contributing guide](./CONTRIBUTING.md).
