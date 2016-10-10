package net.entelijan

import java.text.DecimalFormat

/**
  * Created by wwagner4 on 10/10/16.
  */
object LogTryout extends App {

  val t = 3.0
  val start = 5.0
  val stop = -1.0


  val f = FunctionFactory.lin(start, stop, t)
  val r = (0.0 to(t + 0.5, 0.2)).map(x => (x, f(x)))

  r.foreach { case (x, y) => println(f"$x%.6f;$y%.6f") }
}

object FunctionFactory {

  def log(start: Double, stop: Double, time: Double): Double => Double = {

    val rest = math.abs(start - stop) / 2000.0
    val a = math.pow(math.E, -math.log(rest) / time)
    val d = start - stop

    x => {
      if (x < time) d * math.pow(a, -x) - x * rest / time + stop
      else stop
    }

  }

  def lin(start: Double, stop: Double, time: Double): Double => Double = {

    x => {
      if (x < time) start
      else stop
    }

  }
}
