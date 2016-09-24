package net.entelijan.experiments

import net.entelijan.SoundExperiment
import net.entelijan.Nineth
import doctus.sound.DoctusSoundAudioContext
import doctus.sound.StartStoppable
import net.entelijan.SoundUtil
import doctus.sound.WaveType_Triangle

case class FmSynthExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "fm-synth"

  val frequencies = List(400.0, 500.0, 600.0)
  val lfoFreqs = List(10, 20, 50)
  
  def start(nineth: Nineth): Unit = {
    val (freq, lfoFreq) = SoundUtil.xyParams(frequencies, lfoFreqs)(nineth)
    val inst = Instrument(freq, lfoFreq)
    val  now = ctx.currentTime
    inst.start(now)
    inst.stop(now + 1.0)
    
  }

  def stop(): Unit = {
    // Nothing to do
  }

  case class Instrument(freq: Double, lfoFrq: Double) extends StartStoppable {
    
    val oscil = ctx.createNodeSourceOscil(WaveType_Triangle)
    val gain = ctx.createNodeThroughGain()
    val sink = ctx.createNodeSinkLineOut

    val gainCtrl = ctx.createNodeControlAdsr(0.1, 0.1, 0.2, 1.0, 0.5)
    val oscilFmLfoCtrl = ctx.createNodeControlLfo(WaveType_Triangle, lfoFrq, freq * 0.7)
    val oscilFmOffsetCtrl = ctx.createNodeControlConstant(freq)

    gainCtrl >-gain.gain
    oscilFmOffsetCtrl >- oscil.frequency
    oscilFmLfoCtrl >- oscil.frequency

    oscil >- gain >- sink

    oscilFmLfoCtrl.start(0.0)

    def start(time: Double): Unit = {
      oscil.start(time)
      gainCtrl.start(time)
    }

    def stop(time: Double): Unit = {
      oscil.stop(time + 5)
      gainCtrl.stop(time)
      oscilFmLfoCtrl.stop(time + 5)
    }

  }

}