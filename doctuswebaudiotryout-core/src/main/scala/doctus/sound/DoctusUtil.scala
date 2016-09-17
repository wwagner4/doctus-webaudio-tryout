// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package doctus.sound

/**
 * General usable utility methods
 */
object DoctusUtil {

  /**
   * Linear mapping of values from one to another interval
   */
  def map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double = {
    require(value >= start1)
    require(value <= stop1)
    require(start1 <= stop1)
    require(start2 <= stop2)

    (stop2 - start2) * (value - start1) / (stop1 - start1) + start2
    
  }

}
