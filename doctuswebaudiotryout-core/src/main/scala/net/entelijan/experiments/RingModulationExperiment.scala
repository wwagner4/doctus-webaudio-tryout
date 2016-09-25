package net.entelijan.experiments

import doctus.sound.{DoctusSoundAudioContext, StartStoppable, WaveType_Sawtooth, WaveType_Sine}
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * Example for ring modulation. https://en.wikibooks.org/wiki/Sound_Synthesis_Theory/Modulation_Synthesis#Ring_Modulation
  */
case class RingModulationExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "ring modulation"

  val freqList = List(300.0, 400.0, 500.0)
  val modFreqList = List(100.0, 401.0, 600.0)

  var instOpt = Option.empty[Instrument]

  def start(nineth: Nineth): Unit = {
    val (f, mf) = SoundUtil.xyParams(freqList, modFreqList)(nineth)
    val inst = Instrument(f, mf)
    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument(freq: Double, modFreq: Double) extends StartStoppable {

    val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
    val oscilFrqCtrl = ctx.createNodeControlConstant(freq)

    val gainRingMod = ctx.createNodeThroughGain
    val gainGainCtrl = ctx.createNodeControlLfo(WaveType_Sawtooth)
    val lfoFrqCtrl = ctx.createNodeControlConstant(modFreq)

    val gainAdsr = ctx.createNodeThroughGain
    val adsr = ctx.createNodeControlAdsr(0.2, 0.0, 1.0, 0.5)


    val sink = ctx.createNodeSinkLineOut

    oscilFrqCtrl >- oscil.frequency

    gainGainCtrl >- gainRingMod.gain
    lfoFrqCtrl >- gainGainCtrl.frequency

    adsr >- gainAdsr.gain

    oscil >- gainRingMod >- gainAdsr >- sink

    def start(time: Double): Unit = {
      oscil.start(time)
      gainGainCtrl.start(time)
      adsr.start(time)
    }

    def stop(time: Double): Unit = {
      oscil.stop(time + 3)
      gainGainCtrl.stop(time + 3)
      adsr.stop(time)
    }
  }


}
