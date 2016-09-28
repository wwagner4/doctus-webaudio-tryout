package net.entelijan.experiments

import doctus.sound.{DoctusSoundAudioContext, StartStoppable}
import net.entelijan.{Nineth, SoundExperiment}

/**
  * Creates some sounds using modes
  */
case class ModalSynthesisExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment{

  def title: String = "modal synthesis"

  var instOpt = Option.empty[Instrument]

  def start(nineth: Nineth): Unit = {
    val inst = Instrument(444, 1)
    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)

  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument(frequency: Double, lifetime: Double) extends  StartStoppable {

    def start(time: Double): Unit = {
      println("starting freq:%.2f lifet:%.2f" format(frequency, lifetime))
    }

    def stop(time: Double): Unit = {
      println("stopped")
    }
  }
}
