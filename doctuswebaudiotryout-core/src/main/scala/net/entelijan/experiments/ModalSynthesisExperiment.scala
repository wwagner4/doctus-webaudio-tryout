package net.entelijan.experiments

import doctus.sound.DoctusSoundAudioContext
import net.entelijan.{Nineth, SoundExperiment}

/**
  * Creates some sounds using modes
  */
case class ModalSynthesisExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment{

  def title: String = "modal synthesis"

  def start(nineth: Nineth): Unit = {
    println("starting " + nineth)
  }

  def stop(): Unit = {
    println("stopping")
  }
}
