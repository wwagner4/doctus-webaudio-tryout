package net.entelijan

import org.scalatest.FunSuite

class PinkNoiseFunSuite extends FunSuite {

 test("create pink noise") {
    val vp = NoisePink()
    for (i <- 1 to 300) {
      val x = vp.nextValue
      println(">> %6.3f" format x)
    }
  }

}