package net.entelijan.experiments

import net.entelijan.SoundExperiment
import doctus.sound.DoctusSoundAudioContext
import doctus.sound.WaveType_Triangle
import doctus.sound.NodeSourceOscil
import doctus.sound.WaveType_Sine
import doctus.sound.StartStoppable
import net.entelijan.SoundUtil

case class PanningExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {
  
  def title: String = "panning"
  
  var oscilOpt = Option.empty[StartStoppable]
  var panCtrlOpt = Option.empty[StartStoppable]
  var adsrCtrlOpt = Option.empty[StartStoppable]
  
  val panningFreq = List(0.5, 2.0, 10.0)
  val frequencies = List(300.0, 300.0 * 3 / 2, 300 * 2)

  def start(nineth: net.entelijan.Nineth): Unit = {
    
    val (pf, frq) = SoundUtil.xyParams(panningFreq, frequencies)(nineth)
    
    val oscil = ctx.createNodeSourceOscil(WaveType_Triangle)
    val pan = ctx.createNodeThroughPan()
    val gain = ctx.createNodeThroughGain()
    val sink = ctx.createNodeSinkLineOut 
    
    val panCtrl = ctx.createNodeControlLfo(WaveType_Sine, pf, 1.0)
    val frqCtrl = ctx.createNodeControlConstant(frq)
    val adsrCtrl = ctx.createNodeControlAdsr(0.2, 0.0, 1.0, 0.2, 1.0)
    
    panCtrl >- pan.pan
    frqCtrl >- oscil.frequency
    adsrCtrl >- gain.gain
    
    oscil >- pan >- gain >- sink

    val now = ctx.currentTime
    oscil.start(now)
    panCtrl.start(now)
    adsrCtrl.start(now)
    
    oscilOpt = Some(oscil)
    panCtrlOpt = Some(panCtrl)
    adsrCtrlOpt = Some(adsrCtrl)
  }
  
  def stop(): Unit = {
    val now = ctx.currentTime
    oscilOpt.foreach { _.stop(now + 5) }
    panCtrlOpt.foreach { _.stop(now + 5) }
    adsrCtrlOpt.foreach { _.stop(now) }
  }
  
}