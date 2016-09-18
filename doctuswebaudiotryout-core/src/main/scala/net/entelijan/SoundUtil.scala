// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

object SoundUtil {

  def metalHarmonics(baseFreq: Double, length: Int): List[Double] = {
    val sqrt2 = math.sqrt(2.0)
  
    def metalHarmonics(latestFreq: Double, resultSoFar: List[Double]):List[Double] = {
      if (resultSoFar.size >= length) {
        resultSoFar.reverse
      } else {
        val f = latestFreq * sqrt2
        metalHarmonics(f, f :: resultSoFar)
      }     
    }
    metalHarmonics(baseFreq, List.empty[Double])
  }

  def logarithmicDecay(base: Double)(x: Int): Double = math.pow(base, -x)

  def xyParams[A, B](x: Seq[A], y: Seq[B]): Nineth => (A, B) = {

    case N_00 => (x(0), y(0))
    case N_01 => (x(0), y(1))
    case N_02 => (x(0), y(2))

    case N_10 => (x(1), y(0))
    case N_11 => (x(1), y(1))
    case N_12 => (x(1), y(2))

    case N_20 => (x(2), y(0))
    case N_21 => (x(2), y(1))
    case N_22 => (x(2), y(2))

  }



}
