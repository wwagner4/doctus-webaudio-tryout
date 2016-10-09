package net.entelijan

import ddf.minim.Minim
import ddf.minim.javasound.JSMinim
import ddf.minim.ugens.{Oscil, Waves}
import doctus.sound.{FileLoaderUserHome}

/**
  * Tryout the usage of minim
  * adsr >- gain.gain
  * oscil >- gain >- sink
  */
object MinimAdsrTryout extends App{

  private val minim = {
    val fileLoader = FileLoaderUserHome()
    val serviceProvider = new JSMinim(fileLoader)
    new Minim(serviceProvider)
  }
  println("created minim")

  val oscil = new Oscil(500f, 1.0f, Waves.SINE)
  println("created Oscil")

  val lineOut =  minim.getLineOut()
  lineOut.printControls()
  println("created lineOut")

  pause(1.0)
  oscil.patch(lineOut)
  println("patched oscil to lineOut")

  pause(2.0)

  minim.dispose()
  println("disposed minim")

  def pause(timeInSeconds: Double): Unit = {
    Thread.sleep((timeInSeconds * 1000.0).toLong)
  }

}
