package net.entelijan.experiments

import net.entelijan.SoundExperiment
import doctus.sound.DoctusSoundAudioContext

case class PanningExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {
  
  def title: String = "panning"

  def start(nienth: net.entelijan.Nineth): Unit = {
    println("started")
  }
  
  def stop(): Unit = {
    println("stopped")
  }
  
}