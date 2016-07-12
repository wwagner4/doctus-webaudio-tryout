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

  def logDecay(x: Int)(base: Double): Double = math.pow(base, -x)

}
