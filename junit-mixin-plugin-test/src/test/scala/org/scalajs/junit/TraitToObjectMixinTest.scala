package org.scalajs.junit

import java.util.concurrent.atomic.AtomicInteger

import org.junit._
import org.junit.Assert._

object TraitToObjectMixinTest extends TraitToObjectMixinTest2 {
  val count = new AtomicInteger()

  @AfterClass def afterClass(): Unit = {
    assertEquals(2, count.get)
    count.set(0)
  }
}

class TraitToObjectMixinTest {
  @Test def test1(): Unit = {
    TraitToObjectMixinTest.count.incrementAndGet()
  }
}

trait TraitToObjectMixinTest2 {
  @BeforeClass def test2(): Unit = {
    TraitToObjectMixinTest.count.incrementAndGet()
  }
}
