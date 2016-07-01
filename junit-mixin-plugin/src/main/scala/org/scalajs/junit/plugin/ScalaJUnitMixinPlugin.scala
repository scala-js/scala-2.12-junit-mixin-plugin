package org.scalajs.junit.plugin

import scala.tools.nsc._
import scala.tools.nsc.plugins.{
  Plugin => NscPlugin, PluginComponent => NscPluginComponent
}

class ScalaJUnitMixinPlugin(val global: Global) extends NscPlugin {

  // Disable the warnings emitted during mixin
  global.settings.nowarnDefaultJunitMethods.tryToSet(List("true"))

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
    override val runsBefore: List[String] = List("cleanup", "junit-inject")

    protected def newTransformer(unit: CompilationUnit): Transformer =
      new ScalaJUnitMixinPluginTransformer

    class ScalaJUnitMixinPluginTransformer extends Transformer {

      import rootMirror.getRequiredClass

      private val junitMethodAnnotations = List(
          getRequiredClass("org.junit.Test"),
          getRequiredClass("org.junit.Before"),
          getRequiredClass("org.junit.After"),
          getRequiredClass("org.junit.BeforeClass"),
          getRequiredClass("org.junit.AfterClass"),
          getRequiredClass("org.junit.Ignore")
      )

      override def transform(tree: Tree): Tree = tree match {
        case tree: ClassDef =>
          val classSym = tree.symbol.asClass
          val missingMethods = classSym.info.members.filter { member =>
            member.isMethod &&
            !member.isAbstract &&
            classSym != member.owner &&
            junitMethodAnnotations.exists(member.hasAnnotation)
          }
          if (missingMethods.nonEmpty) {
            val impl = tree.impl
            val bridges =
              missingMethods.map(sym => mkBridge(sym.asMethod, classSym))
            val newBody = tree.impl.body ++ bridges
            val newImpl =
              treeCopy.Template(impl, impl.parents, impl.self, newBody)
            treeCopy.ClassDef(tree, tree.mods, tree.name, tree.tparams, newImpl)
          } else {
            tree
          }

        case _ =>
          super.transform(tree)
      }

      private def mkBridge(sym: MethodSymbol, classSym: ClassSymbol): DefDef = {
        val name = sym.name.toTermName

        val ddefSym = classSym.newMethod(name)
        val ddefInfo = MethodType(sym.info.params.map(_.cloneSymbol(ddefSym)),
            sym.info.resultType)
        ddefSym.setInfo(ddefInfo)
        ddefSym.flags = sym.flags
        ddefSym.flags += Flag.OVERRIDE
        sym.annotations.foreach(ddefSym.addAnnotation)

        val select = Select(Super(This(classSym), typeNames.EMPTY), name)
        select.setSymbol(sym)
        val rhs = Apply(select, ddefSym.info.params.map(gen.mkAttributedIdent))
        rhs.setSymbol(sym)

        typer.typedDefDef(newDefDef(ddefSym, rhs)())
      }
    }
  }
}
