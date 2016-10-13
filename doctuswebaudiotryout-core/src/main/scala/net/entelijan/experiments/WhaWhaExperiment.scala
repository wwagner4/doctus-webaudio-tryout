// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * A Filter that changes its cutoff frequency controlled by an ADSR envelope
  */
case class WhaWhaExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  var inst = Option.empty[Inst]

  def title: String = "wha wha"

  private val baseFreq = 300.0
  private val freqList = Stream.iterate(baseFreq)(f => f * 1.5).take(3).toList
  private val whaWhaFreqList = List(2, 4, 7)

  def start(nineth: Nineth): Unit = {
    val (freq, whaWhaFreq) = SoundUtil.xyParams(freqList, whaWhaFreqList)(nineth)

    val now = ctx.currentTime
    val i = Inst(freq, whaWhaFreq)
    i.start(now)
    inst = Some(i)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    inst.foreach(_.stop(now))
  }

  case class Inst(freq: Double, whaWhaFreq: Double) extends StartStoppable {

    // Create nodes
    val oscil = createOscil(freq)

    val gainAdsr = ctx.createNodeThroughGain
    val gainAdsrCtrl = ctx.createNodeControlAdsr(0.001, 0.1, 0.9, 0.5, 0.5)

    val filter = createWhaWhaFilter(freq, whaWhaFreq)

    val sink = ctx.createNodeSinkLineOut

    // Connect nodes
    gainAdsrCtrl >- gainAdsr.gain

    oscil >- filter >- gainAdsr >- sink

    def start(time: Double): Unit = {
      oscil.start(time)
      gainAdsrCtrl.start(time)
      filter.start(time)
    }

    def stop(time: Double): Unit = {
      gainAdsrCtrl.stop(time)
      oscil.stop(time + 5)
      filter.stop(time + 5)
    }

  }

  def createOscil(freq: Double): NodeSource with StartStoppable = {

    val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)

    val freqCtrl = ctx.createNodeControlConstant(freq)

    freqCtrl >- oscil.frequency

    new NodeSourceContainer with StartStoppable{

      def source: NodeSource = oscil

      def start(time: Double): Unit = oscil.start(time)

      def stop(time: Double): Unit = oscil.stop(time)
    }

  }

  def createWhaWhaFilter(freq: Double, wFreq: Double): NodeThrough with StartStoppable = {

    val nodeFilter = ctx.createNodeThroughFilter(FilterType_Lowpass)
    val filterFreq = ctx.createNodeControlConstant(freq * 1.1)
    val filterQual = ctx.createNodeControlConstant(10)

    val lfo = ctx.createNodeControlLfo(WaveType_Sine)
    val lfoAmpl = ctx.createNodeControlConstant(freq * 0.5)
    val lfoFreq = ctx.createNodeControlConstant(wFreq)

    lfoAmpl >- lfo.amplitude
    lfoFreq >- lfo.frequency

    filterFreq >- nodeFilter.frequency
    lfo >- nodeFilter.frequency
    filterQual >- nodeFilter.quality


    new NodeThroughContainer with StartStoppable {

      def source: NodeSource = nodeFilter

      def sink: NodeSink = nodeFilter

      def start(time: Double): Unit = lfo.start(time)

      def stop(time: Double): Unit = lfo.stop(time)
    }

  }

}
