package net.entelijan

/**
  * Created by wwagner4 on 10/10/16.
  */
object LogTryout extends App {
  val t = 4.0
  val d = 0.00001

  val a = math.pow(math.E, - math.log(d) / t)

  val r = (0.0 to (t, 0.2)).map(x => (x, math.pow(a, - x)))

  r.foreach{case (x, y) => println(f"$x%10.2f -> $y%5.7f")}
}
