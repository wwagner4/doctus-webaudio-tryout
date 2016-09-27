package net.entelijan.experiments

import doctus.sound._
import net.entelijan._

/**
  * Synthesis for the simulation of string instruments.
  *
  * https://en.wikipedia.org/wiki/Karplus–Strong_string_synthesis
  */
case class KarplusStrongExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "karplus-strong"

  var instOpt = Option.empty[Instrument]

  val frequencies = Stream.iterate(400.0)(f => f * math.pow(2, 1.0 / 3))

  def frequencyFrom(nineth: Nineth) = nineth match {
    case N_00 => frequencies(0)
    case N_01 => frequencies(1)
    case N_02 => frequencies(2)

    case N_10 => frequencies(3)
    case N_11 => frequencies(4)
    case N_12 => frequencies(5)

    case N_20 => frequencies(6)
    case N_21 => frequencies(7)
    case N_22 => frequencies(8)
  }

  def start(nineth: Nineth): Unit = {
    val frequency = frequencyFrom(nineth)
    val inst = Instrument(frequency)

    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument(frequency: Double) extends StartStoppable {

    val noise = ctx.createNodeSourceNoise(NoiseType_Pink)
    val gain = ctx.createNodeThroughGain
    val sink = ctx.createNodeSinkLineOut
    val masterGain = createMasterGain(1.0)

    val adsr = ctx.createNodeControlAdsr(0.001, 0.0, 1.0, 0.001)

    adsr >- gain.gain

    noise >- gain >- masterGain >- sink

    def start(time: Double): Unit = {
      noise.start(0.0)
      adsr.start(time)
      val dur = 0.01
      noise.stop(time + dur + 3.0)
      adsr.stop(time + dur)
    }

    def stop(time: Double): Unit = {
      // Nothing to do
    }

  }

  def createMasterGain(gainVal: Double): NodeThrough = {
    val gain = ctx.createNodeThroughGain
    val gainCtrl = ctx.createNodeControlConstant(gainVal)

    gainCtrl >- gain.gain

    gain
  }

}
