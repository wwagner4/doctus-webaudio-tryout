package net.entelijan.experiments

import net.entelijan.SoundExperiment
import net.entelijan.Nineth
import doctus.sound.DoctusSoundAudioContext

case class DelayExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "delay"

  def start(nineth: Nineth): Unit = {
    val now = ctx.currentTime
    println("started at %.2f" format now)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    println("stopped at %.2f" format now)
  }

}