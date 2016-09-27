package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * Exploring different kinds of filters for different frequencies
  */
case class FilterExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "filter"

  val frequencies = List(100.0, 400.0, 700.0)
  val types: List[FilterType] = List(FilterType_Lowpass, FilterType_Highpass, FilterType_Bandpass)

  def start(nineth: Nineth): Unit = {
    val (f, t) = SoundUtil.xyParams(frequencies, types)(nineth)
    println("started %.2f %s" format(f, t))


  }

  def stop(): Unit = {
    println("stopped")

  }
}
