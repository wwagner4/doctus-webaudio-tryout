package net.entelijan

/**
  * Created by wwagner4 on 10/10/16.
  */
object LogTryout extends App {

  val t = 4.0
  val d = 0.000001
  val start = 2.0
  val stop = 1.0


  val f = FunctionFactory.logDecent(start, stop, t, d)
  val r = (0.0 to (t + 0.5, 0.2)).map(x => (x, f(x)))


  r.foreach{case (x, y) => println(f"$x%10.2f -> $y%11.7f")}
}

object FunctionFactory {

  def logDecent(start: Double, stop: Double, time: Double, rest: Double): Double => Double = {

    val a = math.pow(math.E, - math.log(rest) / time)
    val d = start - stop

    (x: Double) => {
      if (x < time) d * math.pow(a, - x) - x * rest / time + stop
      else stop
    }

  }



}
