package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

import scala.util.Random

/**
  * Creates some sounds using modes
  */
case class ModalSynthesisExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  val ran = Random

  def title: String = "modal synthesis"

  var instOpt = Option.empty[Instrument]

  def start(nineth: Nineth): Unit = {
    val inst = Instrument(nineth)
    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)

  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  val freqFactors = List(2.0, math.pow(2, 1.0 / 10), 3.0 / 4.0)
  val freqs = List(300.0, 400.0, 600.0)


  case class Instrument(nineth: Nineth) extends StartStoppable {

    val (freq, fact) = SoundUtil.xyParams(freqs, freqFactors)(nineth)

    val freqSeq = Stream.iterate(freq)(f => (f * fact) * ranFreqDetune).take(6)

    val modes = freqSeq.map(f => Mode(f, ranAmpl, ranlifetime))

    val masterGain = gain(0.3)

    val sink = ctx.createNodeSinkLineOut

    modes.foreach(mode => mode >- masterGain)
    masterGain >- sink

    def start(time: Double): Unit = {
      modes.foreach(mode => mode.start(time))
    }

    def stop(time: Double): Unit = {
      // Nothing to do
    }

    def ranAmpl = 0.1 + ran.nextDouble() * 1.4
    def ranlifetime = 0.1 + ran.nextDouble() * 1.0
    def ranFreqDetune = 0.95 + ran.nextDouble() * 0.1

  }

  case class Mode(freq: Double, ampl: Double, lifetime: Double) extends NodeSourceContainer with StartStoppable {

    val attack = 0.001
    val maxLifetime = 5.0 // seconds

    val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
    val gain = ctx.createNodeThroughGain

    val freqCtrl = ctx.createNodeControlConstant(freq)
    val decayCtrl = ctx.createNodeControlAdsr(attack, 0.0, 1.0, lifetime, gain = ampl, trend = Trend_Exponential(lifetime))

    freqCtrl >- oscil.frequency
    decayCtrl >- gain.gain

    oscil >- gain

    def start(time: Double): Unit = {
      oscil.start(time)
      decayCtrl.start(time)
      decayCtrl.stop(time + attack + 0.0001)
      oscil.stop(time + maxLifetime)
    }

    def stop(time: Double): Unit = {
      // Nothing to do here
    }

    def source: NodeSource = gain

  }

  def gain(gainVal: Double): NodeThrough = {

    val gain = ctx.createNodeThroughGain
    val value = ctx.createNodeControlConstant(gainVal)

    value >- gain.gain

    gain
  }

}
