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
    println("start %.2f" format now)
    val i = Inst(freq)
    i.start(now)
    inst = Some(i)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    println("stop %.2f" format now)
    inst.foreach(_.stop(now))
  }

  case class Inst(freq: Double) extends StartStoppable {

    val freqCtrl = ctx.createNodeControlConstant(400)
    val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)
    val sink = ctx.createNodeSinkLineOut
    val gain = ctx.createNodeThroughGain
    val gainCtrl = ctx.createNodeControlAdsr(0.5, 0.1, 0.5, 1.5)
    val gain1 = ctx.createNodeThroughGain
    val gainCtrl1 = ctx.createNodeControlConstant(0.1)


    freqCtrl >- oscil.frequency
    gainCtrl >- gain.gain
    gainCtrl1 >- gain1.gain
    oscil >- gain >- gain1 >- sink

    def start(time: Double): Unit = {
      oscil.start(time)
      gainCtrl.start(time)
    }

    def stop(time: Double): Unit = {
      gainCtrl.stop(time)
      oscil.stop(time + 5)
    }

  }

}
