package org.scalajs.junit.plugin

import scala.tools.nsc._
import scala.tools.nsc.plugins.{
  Plugin => NscPlugin, PluginComponent => NscPluginComponent
}

class ScalaJUnitMixinPlugin(val global: Global) extends NscPlugin {

  val name: String = "Scala 2.12 JUnit mixin plugin"

  val components: List[NscPluginComponent] =
    List(ScalaJUnitMixinPluginComponent)

  val description: String = "Makes JUnit tests in traits invokable in Scala 2.12."

  object ScalaJUnitMixinPluginComponent
      extends plugins.PluginComponent with transform.Transform {

    val global: Global = ScalaJUnitMixinPlugin.this.global
    import global._

    val phaseName: String = "junit-make-mixin-bridges"
    val runsAfter: List[String] = List("mixin")
    override val runsBefore: List[String] = List("cleanup")

    protected def newTransformer(unit: CompilationUnit): Transformer =
      new ScalaJUnitMixinPluginTransformer

    class ScalaJUnitMixinPluginTransformer extends Transformer {
      override def transform(tree: Tree): Tree = tree match {
        case _ =>
          super.transform(tree)
      }
    }
  }
}
