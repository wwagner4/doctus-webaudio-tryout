package net.entelijan

/**
  * Created by wwagner4 on 10/10/16.
  */
object LogTryout extends App {

  val t = 4.0
  val d = 0.0001


  val f = FunctionFactory.logDecent(t, d)
  val r = (0.0 to (t + 0.5, 0.2)).map(x => (x, f(x)))


  r.foreach{case (x, y) => println(f"$x%10.2f -> $y%11.7f")}
}

object FunctionFactory {

  def logDecent(t: Double, d: Double): Double => Double = {

    val a = math.pow(math.E, - math.log(d) / t)

    (x: Double) => {
      if (x < t) math.pow(a, - x) - x * d / t
      else 0.0
    }

  }



}
