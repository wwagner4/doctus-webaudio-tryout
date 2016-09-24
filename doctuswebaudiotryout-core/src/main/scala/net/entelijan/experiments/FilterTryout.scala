// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * A Filter that changes its cutoff frequency controlled by an ADSR envelope
  */
case class FilterTryout(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  var inst = Option.empty[Inst]

  def title: String = "filter"

  private val baseFreq = 300.0
  private val freqMulti = 1.5
  private val freqList = List(baseFreq, baseFreq * freqMulti, baseFreq * freqMulti * freqMulti)
  private val filterFreqMultipleList = List(1.5, 3.0, 6.0)

  def start(nineth: Nineth): Unit = {
    val (freq, filterFreqMultiple) = SoundUtil.xyParams(freqList, filterFreqMultipleList)(nineth)

    val now = ctx.currentTime
    val i = Inst(freq, filterFreqMultiple)
    i.start(now)
    inst = Some(i)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    inst.foreach(_.stop(now))
  }

  case class Inst(freq: Double, filterFreqMultiple: Double) extends StartStoppable {

    // Create nodes
    val oscilFreqCtrl = ctx.createNodeControlConstant(freq)
    val oscil = ctx.createNodeSourceOscil(WaveType_Square)

    val gainAdsr = ctx.createNodeThroughGain
    val gainAdsrCtrl = ctx.createNodeControlAdsr(0.1, 0.1, 0.9, 4.0)

    val gainMain = ctx.createNodeThroughGain
    val gainMainCtrl = ctx.createNodeControlConstant(0.4)

    val filter = ctx.createNodeThroughFilter(FilterType_Lowpass)
    val filterFreqAdsrCtrl = ctx.createNodeControlAdsr(0.001, 0, 1.0, 4.0, freq * filterFreqMultiple)
    val filterFreqConstCtrl = ctx.createNodeControlConstant(freq * 0.6)

    val sink = ctx.createNodeSinkLineOut

    // Connect nodes
    gainMainCtrl >- gainMain.gain

    gainAdsrCtrl >- gainAdsr.gain

    filterFreqAdsrCtrl >- filter.frequency
    filterFreqConstCtrl >- filter.frequency

    oscilFreqCtrl >- oscil.frequency

    oscil >- filter >- gainAdsr >- gainMain >- sink

    def start(time: Double): Unit = {
      oscil.start(time)
      gainAdsrCtrl.start(time)
      filterFreqAdsrCtrl.start(time)
    }

    def stop(time: Double): Unit = {
      gainAdsrCtrl.stop(time)
      filterFreqAdsrCtrl.stop(time)
      oscil.stop(time + 5)
    }

  }

}
