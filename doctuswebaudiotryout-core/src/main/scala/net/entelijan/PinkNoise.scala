package net.entelijan

/**
  * Pink noise
  * Migrated from Minim
  *
  * https://github.com/ddf/Minim/blob/master/src/ddf/minim/signals/PinkNoise.java
  */
object PinkNoise {

  def iterator: Iterator[Double] = {
    val maxKey = 0x1f
    val range = 128
    val poles = 6
    var key = 0
    var maxSumEver = 90.0
    val whiteValues = for (i <- (0 until poles).toArray) yield {
      (math.random * Long.MaxValue) % (range / poles)
    }

    def pink: Double = {
      var last_key = key;
      var sum = 0.0;
      key += 1;
      if (key > maxKey) key = 0;
      val diff = last_key ^ key;
      for (i <- 0 until poles) {
        if ((diff & (1 << i)) != 0) {
          whiteValues(i) = (math.random * Long.MaxValue) % (range / poles);
        }
        sum += whiteValues(i);
      }
      if (sum > maxSumEver) maxSumEver = sum;
      sum = 2.0 * (sum / maxSumEver) - 1.0;
      sum
    }

    new Iterator[Double] {
      def hasNext: Boolean = true
      def next(): Double = pink
    }

  }

}
