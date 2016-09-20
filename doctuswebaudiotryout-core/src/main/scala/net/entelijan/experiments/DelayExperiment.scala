package net.entelijan.experiments

import net.entelijan.SoundExperiment
import net.entelijan.Nineth
import doctus.sound.DoctusSoundAudioContext
import doctus.sound.StartStoppable

case class DelayExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "delay"
  
  var instOpt = Option.empty[Inst]
  
  def start(nineth: Nineth): Unit = {
    val now = ctx.currentTime
    val inst = Inst()
    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach { _.stop(now) }
  }

  case class Inst() extends StartStoppable {
    
    def start(time: Double): Unit = {
      println("INST started at %.2f" format time)
    }

    def stop(time: Double): Unit = {
      println("INST stopped at %.2f" format time)
    }
    
  }

}