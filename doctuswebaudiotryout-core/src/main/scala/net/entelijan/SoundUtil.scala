package net.entelijan

object SoundUtil {

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

  def logDecay(x: Int)(base: Double): Double = math.pow(base, -x)

}