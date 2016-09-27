package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * Exploring different kinds of filters for different frequencies
  */
case class FilterExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "filter"

  val frequencies = List(300.0, 600.0, 900.0)
  val types: List[FilterType] = List(FilterType_Lowpass, FilterType_Highpass, FilterType_Bandpass)

  var instOpt = Option.empty[Instrument]

  def start(nineth: Nineth): Unit = {
    val (f, t) = SoundUtil.xyParams(frequencies, types)(nineth)

    val inst = new Instrument(f, t)
    val now = ctx.currentTime

    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument(frequency: Double, filterType: FilterType) extends StartStoppable {

    val noise = ctx.createNodeSourceNoise(NoiseType_White)

    val filter = createFilter(frequency, 3, filterType)
    val sink = ctx.createNodeSinkLineOut

    noise >- filter >- sink

    def start(time: Double): Unit = {
      noise.start(time)
      println("started %.2f %s" format(frequency, filterType))
    }

    def stop(time: Double): Unit = {
      noise.stop(time)
      println("stopped")
    }

    def createFilter(frequency: Double, quality: Double, filterType: FilterType): NodeThroughFilter = {

      val filter = ctx.createNodeThroughFilter(filterType)
      val ctrlFreq = ctx.createNodeControlConstant(frequency)
      val ctrlQ = ctx.createNodeControlConstant(quality)

      ctrlFreq >- filter.frequency
      ctrlQ >- filter.quality

      filter
    }

  }

}
