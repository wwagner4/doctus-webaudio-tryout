package net.entelijan

import doctus.sound.TransitionFunctionFactory

/**
  * Created by wwagner4 on 10/10/16.
  */
object LogTryout extends App {

  val t = 3.0
  val start = 2.0
  val stop = 10.0


  val f = TransitionFunctionFactory.lin(start, stop, t)
  val r = (0.0 to(t + 0.5, 0.2)).map(x => (x, f(x)))

  r.foreach { case (x, y) => println(f"$x%.6f;$y%.6f") }
}

