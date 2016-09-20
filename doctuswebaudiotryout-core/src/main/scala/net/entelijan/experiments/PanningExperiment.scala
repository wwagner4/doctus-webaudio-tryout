package net.entelijan.experiments

import net.entelijan.SoundExperiment
import doctus.sound.DoctusSoundAudioContext
import doctus.sound.WaveType_Triangle
import doctus.sound.NodeSourceOscil
import doctus.sound.WaveType_Sine
import doctus.sound.StartStoppable

case class PanningExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {
  
  def title: String = "panning"
  
  var oscilOpt = Option.empty[NodeSourceOscil]
  var panCtrlOpt = Option.empty[StartStoppable]

  def start(nienth: net.entelijan.Nineth): Unit = {
    
    val oscil = ctx.createNodeSourceOscil(WaveType_Triangle)
    val pan = ctx.createNodeThroughPan
    val sink = ctx.createNodeSinkLineOut 
    
    val panCtrl = ctx.createNodeControlLfo(WaveType_Sine, 0.2, 1.0)
    
    panCtrl >- pan.pan
    
    oscil >- pan >- sink

    val now = ctx.currentTime
    oscil.start(now)
    panCtrl.start(now)
    
    oscilOpt = Some(oscil)
    panCtrlOpt = Some(panCtrl)
    println("started")
  }
  
  def stop(): Unit = {
    val now = ctx.currentTime
    oscilOpt.foreach { _.stop(now) }
    panCtrlOpt.foreach { _.stop(now) }
    println("stopped")
  }
  
}