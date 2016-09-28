package net.entelijan.experiments

import doctus.sound._
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

    val mode = Mode(frequency, 1.0, lifetime)
    val sink = ctx.createNodeSinkLineOut

    mode >- sink

    def start(time: Double): Unit = {
      mode.start(time)
    }

    def stop(time: Double): Unit = {
      // Nothing to do
    }
  }

  case class Mode(freq: Double, ampl: Double, lifetime: Double) extends NodeSourceContainer with StartStoppable {

    val oscil = ctx.createNodeSourceOscil(WaveType_Sine)


    def start(time: Double): Unit = {
      println("stared mode at %.2f %s" format (time, this))
      oscil.start(time)
      oscil.stop(time + 1.5)
    }

    def stop(time: Double): Unit = {
       // Nothing to do here
    }

    def source: NodeSource = oscil

  }

}
