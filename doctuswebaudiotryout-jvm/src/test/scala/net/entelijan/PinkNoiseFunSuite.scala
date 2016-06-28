package net.entelijan

import org.scalatest.FunSuite

class PinkNoiseFunSuite extends FunSuite {

 test("create pink noise") {
    val iter = PinkNoise.iterator
    for (i <- 1 to 300) {
      val x = iter.next()
      println(">> %5.3f" format x)
    }
  }

}