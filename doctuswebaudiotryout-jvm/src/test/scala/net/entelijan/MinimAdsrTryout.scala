package net.entelijan

import ddf.minim.Minim
import ddf.minim.javasound.JSMinim
import ddf.minim.ugens.{ADSR, Gain, Oscil, Waves}
import doctus.sound.FileLoaderUserHome

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
  println("--- created minim")

  val oscil = new Oscil(500f, 1.0f, Waves.SINE)
  oscil.printInputs()
  println("--- created Oscil")

  val gain = new Gain(0.0f)
  gain.printInputs()
  println("--- created Gain")

  val adsr = new ADSR(1.0f, 1.0f, 1.0f, 0.2f, 2.0f)
  gain.printInputs()
  println("--- created ADSR")

  val lineOut =  minim.getLineOut()
  lineOut.printControls()
  println("--- created lineOut")

  pause(1.0)
  adsr.patch(gain.gain)

  oscil.patch(gain)
  gain.patch(lineOut)
  println("patched oscil via gain to lineOut")

  pause(2.0)

  minim.dispose()
  println("disposed minim")

  def pause(timeInSeconds: Double): Unit = {
    Thread.sleep((timeInSeconds * 1000.0).toLong)
  }

}
