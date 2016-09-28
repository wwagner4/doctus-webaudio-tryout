package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment}

/**
  * Creates some sounds using modes
  */
case class ModalSynthesisExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "modal synthesis"

  var instOpt = Option.empty[Instrument]

  def start(nineth: Nineth): Unit = {
    val inst = Instrument(444, 0.1)
    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)

  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument(frequency: Double, lifetime: Double) extends StartStoppable {

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

    val attack = 0.001
    val maxLifetime = 5.0 // seconds

    val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
    val gain = ctx.createNodeThroughGain

    val decay = ctx.createNodeControlAdsr(attack, 0.0, 1.0, lifetime, trend = Trend_Exponential(lifetime))

    decay >- gain.gain

    oscil >- gain

    def start(time: Double): Unit = {
      oscil.start(time)
      decay.start(time)
      decay.stop(time + attack + 0.0001)
      oscil.stop(time + maxLifetime)
    }

    def stop(time: Double): Unit = {
      // Nothing to do here
    }

    def source: NodeSource = gain

  }

}
