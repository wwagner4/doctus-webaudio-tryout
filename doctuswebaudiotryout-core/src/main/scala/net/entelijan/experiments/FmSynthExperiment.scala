package net.entelijan.experiments

import net.entelijan.SoundExperiment
import net.entelijan.Nineth
import doctus.sound.{DoctusSoundAudioContext, StartStoppable, WaveType_Sine, WaveType_Triangle}
import net.entelijan.SoundUtil

case class FmSynthExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "fm-synth"

  val frequencies = List(400.0, 500.0, 600.0)
  val lfoFreqs = List(10, 20, 50)

  val duration = 1.3
  
  def start(nineth: Nineth): Unit = {
    val (freq, lfoFreq) = SoundUtil.xyParams(frequencies, lfoFreqs)(nineth)
    val inst = Instrument(freq, lfoFreq)
    val  now = ctx.currentTime
    inst.start(now)
    inst.stop(now + duration)
  }

  def stop(): Unit = {
    // Nothing to do
  }

  case class Instrument(freq: Double, lfoFreq: Double) extends StartStoppable {

    val lfoFreq1 = 100
    
    val oscil = ctx.createNodeSourceOscil(WaveType_Triangle)
    val gain = ctx.createNodeThroughGain
    val sink = ctx.createNodeSinkLineOut

    val gainCtrl = ctx.createNodeControlAdsr(0.1, 0.1, 0.2, 1.0, 0.5)
    val oscilFmLfoCtrl0 = ctx.createNodeControlLfo(WaveType_Triangle)
    val lfoFreqCtrl0 = ctx.createNodeControlConstant(lfoFreq)
    val lfoAmplCtrl0 = ctx.createNodeControlConstant(freq * 0.7)

    val oscilFmLfoCtrl1 = ctx.createNodeControlLfo(WaveType_Sine)
    val lfoFreqCtrl1 = ctx.createNodeControlConstant(lfoFreq1)
    val lfoAmplCtrl1 = ctx.createNodeControlConstant(lfoFreq * 0.9)

    val oscilFmOffsetCtrl = ctx.createNodeControlConstant(freq)

    oscilFmLfoCtrl1 >- oscilFmLfoCtrl0.frequency
    lfoFreqCtrl0 >- oscilFmLfoCtrl0.frequency
    lfoAmplCtrl0 >- oscilFmLfoCtrl0.amplitude

    lfoFreqCtrl1 >- oscilFmLfoCtrl1.frequency
    lfoAmplCtrl1 >- oscilFmLfoCtrl1.amplitude

    gainCtrl >-gain.gain
    oscilFmOffsetCtrl >- oscil.frequency
    oscilFmLfoCtrl0 >- oscil.frequency

    oscil >- gain >- sink

    oscilFmLfoCtrl0.start(0.0)
    oscilFmLfoCtrl1.start(0.0)

    def start(time: Double): Unit = {
      oscil.start(time)
      gainCtrl.start(time)
    }

    def stop(time: Double): Unit = {
      oscil.stop(time + 5)
      gainCtrl.stop(time)
      oscilFmLfoCtrl0.stop(time + 5)
      oscilFmLfoCtrl1.stop(time + 5)
    }

  }

}