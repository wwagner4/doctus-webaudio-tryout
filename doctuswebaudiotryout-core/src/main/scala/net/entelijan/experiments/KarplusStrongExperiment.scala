package net.entelijan.experiments

import doctus.sound.DoctusSoundAudioContext
import net.entelijan.{Nineth, SoundExperiment}

/**
  * Created by wwagner4 on 25/09/16.
  */
case class KarplusStrongExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment{

  def title: String = "karplus-strong"

  def start(nineth: Nineth): Unit = {
    println("started")
  }

  def stop(): Unit = {
    println("stopped")
  }
}
