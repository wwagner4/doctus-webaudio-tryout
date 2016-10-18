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
  //exp.start(N_01) // 0.001, 0.1, 0.1, 1.3
  exp.start(N_11) // 0.1, 0.1, 0.1, 1.3
  //exp.start(N_21) // 0.6, 0.1, 0.1, 1.3
  pause(1.seconds)
  exp.stop()
  pause(2.seconds)
  ctx.terminate
  pause(2.seconds)

  def pause(duration: FiniteDuration): Unit = {
    Thread.sleep(duration.toMillis)
  }

}
