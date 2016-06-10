package org.scalajs.junit

import java.util.concurrent.atomic.AtomicInteger

import org.junit._
import org.junit.Assert._

object TraitToClassMixinTest {
  val count = new AtomicInteger()

  @AfterClass def afterClass(): Unit = {
    assertEquals(2, count.get())
    count.set(0)
  }
}

class TraitToClassMixinTest extends TraitToClassMixinTest2 {
  @Test def test1(): Unit = {
    TraitToClassMixinTest.count.incrementAndGet()
  }
}

trait TraitToClassMixinTest2 {
  @Test def test2(): Unit = {
    TraitToClassMixinTest.count.incrementAndGet()
  }
}
