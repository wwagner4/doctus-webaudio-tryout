package net.entelijan

import doctus.sound.DoctusSoundAudioContextJvmMinim
import net.entelijan.experiments.AdsrExperiment

import scala.concurrent.duration._

/**
  * Runs minim without GUI
  */
object DoctuswebaudiotryoutJvmNoGui extends App {

  pause(2.seconds)
  val ctx = new DoctusSoundAudioContextJvmMinim()
  pause(2.seconds)

  val exp = AdsrExperiment(ctx)
  exp.start(N_00)
  pause(2.seconds)
  exp.stop()
  pause(2.seconds)
  ctx.terminate
  pause(2.seconds)

  def pause(duration: FiniteDuration): Unit = {
    Thread.sleep(duration.toMillis)
  }

}
