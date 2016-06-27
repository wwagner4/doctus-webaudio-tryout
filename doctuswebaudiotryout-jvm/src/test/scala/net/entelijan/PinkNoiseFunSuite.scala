package net.entelijan

import org.scalatest.FunSuite

class PinkNoiseFunSuite extends FunSuite {

  def pinkNoiseIter: Iterator[Double] = {
    val maxKey = 0x1f
    val range = 128
    val poles = 6
    var key = 0
    var maxSumEver = 90.0
    var whiteValues = for (i <- (0 until poles).toArray) yield {
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

  test("create pink noise") {
    val iter = pinkNoiseIter
    for (i <- 1 to 300) {
      val x = iter.next()
      println(">> %5.3f" format x)
    }
  }

}