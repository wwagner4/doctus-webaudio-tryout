package net.entelijan.experiments

import doctus.sound.DoctusSoundAudioContext
import net.entelijan.{Nineth, SoundExperiment}

/**
  * Example for ring modulation. https://en.wikibooks.org/wiki/Sound_Synthesis_Theory/Modulation_Synthesis#Ring_Modulation
  */
case class RingModulation(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "ring modulation"

  def start(nineth: Nineth): Unit = {
    println("started " + nineth)
  }

  def stop(): Unit = {
    println("stopped")
  }
}
