package net.entelijan

import doctus.sound.DoctusUtil
import org.scalatest.FunSuite

class SoundUtilFunSuite extends FunSuite {



  def metalHarmonics(baseFreq: Double, length: Int): List[Double] = {
    val sqrt2 = math.sqrt(2.0)
  
    def metalHarmonics_(latestFreq: Double, resultSoFar: List[Double]):List[Double] = {
      if (resultSoFar.size >= length) {
        resultSoFar.reverse
      } else {
        val f = latestFreq * sqrt2
        metalHarmonics_(f, f :: resultSoFar)
      }     
    }
    metalHarmonics_(baseFreq, List.empty[Double])
  }


  test("metal harmonics") {
    val hl = metalHarmonics(100, 5)
    assert(hl.size === 5)
    
    assert(hl(0) > 141.3)
    assert(hl(0) < 141.5)

    assert(hl(1) > 199.9)
    assert(hl(1) < 200.1)

    assert(hl(2) > 282.7)
    assert(hl(2) < 282.9)

    assert(hl(3) > 399.9)
    assert(hl(3) < 400.1)

    assert(hl(4) > 565.5)
    assert(hl(4) < 565.7)

  }

}

