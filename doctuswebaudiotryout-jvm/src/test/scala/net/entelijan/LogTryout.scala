package net.entelijan

import java.text.DecimalFormat

/**
  * Created by wwagner4 on 10/10/16.
  */
object LogTryout extends App {

  val t = 3.0
  val start = 2.0
  val stop = 10.0


  val f = FunctionFactory.lin(start, stop, t)
  val r = (0.0 to(t + 0.5, 0.2)).map(x => (x, f(x)))

  r.foreach { case (x, y) => println(f"$x%.6f;$y%.6f") }
}

object FunctionFactory {

  def log(from: Double, to: Double, time: Double): Double => Double = {

    val rest = math.abs(from - to) / 2000.0
    val a = math.pow(math.E, -math.log(rest) / time)
    val d = from - to

    x => {
      if (x < time) d * math.pow(a, -x) - x * rest / time + to
      else to
    }

  }

  def lin(from: Double, to: Double, time: Double): Double => Double = {

    x => {
      if (x < time) from + (to - from) / time * x
      else to
    }

  }
}
