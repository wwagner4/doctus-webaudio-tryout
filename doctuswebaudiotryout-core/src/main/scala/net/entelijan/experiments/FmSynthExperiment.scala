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
  val durations = List(0.2, 1.0, 2.5)
  
  def start(nineth: Nineth): Unit = {
    val (f, d) = SoundUtil.xyParams(frequencies, durations)(nineth)
    val inst = Instrument(f)
    val  now = ctx.currentTime
    inst.start(now)
    inst.stop(now + d)
    
  }

  def stop(): Unit = {
    // Nothing to do
  }

  case class Instrument(freq: Double) extends StartStoppable {
    
    val oscil = ctx.createNodeSourceOscil(WaveType_Triangle)
    val gain = ctx.createNodeThroughGain
    val sink = ctx.createNodeSinkLineOut

    val oscilCtrl = ctx.createNodeControlConstant(freq)
    val gainCtrl = ctx.createNodeControlAdsr(0.1, 0.1, 0.2, 1.0, 0.5)
    
    gainCtrl >-gain.gain
    oscilCtrl >- oscil.frequency
    
    oscil >- gain >- sink

    def start(time: Double): Unit = {
      oscil.start(time)
      gainCtrl.start(time)
    }

    def stop(time: Double): Unit = {
      oscil.stop(time + 2)
      gainCtrl.stop(time)
    }

  }

}