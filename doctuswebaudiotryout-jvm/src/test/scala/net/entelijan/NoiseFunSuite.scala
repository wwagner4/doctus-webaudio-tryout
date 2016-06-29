package net.entelijan

import org.scalatest.FunSuite

class NoiseFunSuite extends FunSuite {

  test("create white noise") {
    val vp = NoiseWhite
    for (i <- 1 to 10) {
      val x = vp.nextValue
      println("white >> %6.3f" format x)
    }
  }

  test("create pink noise") {
    val vp = NoisePink()
    for (i <- 1 to 3000) {
      val x = vp.nextValue
      if (i >= 2990) println("pink >> %6.3f" format x)
    }
  }

  test("create brown noise") {
    val vp = NoiseBrown(44000)
    for (i <- 1 to 3000) {
      val x = vp.nextValue
      if (i >= 2990) println("brown >> %6.3f" format x)
    }
  }

}