// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

/**
 * Pink noise
 * Migrated from Minim
 *
 * https://github.com/ddf/Minim/blob/master/src/ddf/minim/signals/PinkNoise.java
 */
case class NoisePink() extends ValueSequence {

  println("Noise Pink")

  val maxKey = 0x1f
  val range = 128
  val poles = 6
  val whiteValues = for (i <- (0 until poles).toArray) yield {
    (math.random * Long.MaxValue) % (range / poles)
  }

  var key = 0
  var maxSumEver = 90.0

  def nextValue: Double = {
    val last_key = key
    var sum = 0.0
    key += 1
    if (key > maxKey) key = 0
    val diff = last_key ^ key
    for (i <- 0 until poles) {
      if ((diff & (1 << i)) != 0) {
        whiteValues(i) = (math.random * Long.MaxValue) % (range / poles)
      }
      sum += whiteValues(i)
    }
    if (sum > maxSumEver) maxSumEver = sum
    2.0 * (sum / maxSumEver) - 1.0
  }


}
