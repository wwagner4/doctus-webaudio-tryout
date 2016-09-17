// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

import scala.util.Random

/**
  * A Filter that changes its cutoff frequency controlled by an ADSR envelope
  */
case class FilterTryout(ctx: DoctusSoundAudioContext) {

  val freq = 200 + Random.nextDouble() * 300

  var inst = Option.empty[Inst]

  def start(): Unit = {
    val now = ctx.currentTime
    val i = Inst(freq)
    i.start(now)
    inst = Some(i)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    inst.foreach(_.stop(now))
  }

  case class Inst(freq: Double) extends StartStoppable {

    val oscilFreqCtrl = ctx.createNodeControlConstant(freq)
    val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)
    val sink = ctx.createNodeSinkLineOut
    val gainAdsr = ctx.createNodeThroughGain
    val gainAdsrCtrl = ctx.createNodeControlAdsr(0.0005, 0.1, 0.5, 1.5)
    val gainMain = ctx.createNodeThroughGain
    val gainMainCtrl = ctx.createNodeControlConstant(0.1)


    oscilFreqCtrl >- oscil.frequency
    gainAdsrCtrl >- gainAdsr.gain
    gainMainCtrl >- gainMain.gain
    oscil >- gainAdsr >- gainMain >- sink

    def start(time: Double): Unit = {
      oscil.start(time)
      gainAdsrCtrl.start(time)
    }

    def stop(time: Double): Unit = {
      gainAdsrCtrl.stop(time)
      oscil.stop(time + 5)
    }

  }

}
