// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._

import scala.util.Random

/**
  * A Filter that changes its cutoff frequency controlled by an ADSR envelope
  */
case class FilterTryout(ctx: DoctusSoundAudioContext) {

  var inst = Option.empty[Inst]

  def start(): Unit = {
    val now = ctx.currentTime
    val freq = 300 + Random.nextDouble() * 200
    val i = Inst(freq)
    i.start(now)
    inst = Some(i)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    inst.foreach(_.stop(now))
  }

  case class Inst(freq: Double) extends StartStoppable {

    // Create nodes
    val oscilFreqCtrl = ctx.createNodeControlConstant(freq)
    val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)

    val gainAdsr = ctx.createNodeThroughGain
    val gainAdsrCtrl = ctx.createNodeControlAdsr(0.1, 0.1, 0.9, 2.0)

    val gainMain = ctx.createNodeThroughGain
    val gainMainCtrl = ctx.createNodeControlConstant(1.2)

    val filter = ctx.createNodeThroughFilter(FilterType_Lowpass)
    val filterFreqAdsrCtrl = ctx.createNodeControlAdsr(0.001, 0, 1.0, 2.0, freq * 0.6)
    val filterFreqConstCtrl = ctx.createNodeControlConstant(freq * 0.8)

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
