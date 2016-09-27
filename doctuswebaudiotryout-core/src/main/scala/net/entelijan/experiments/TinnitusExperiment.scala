// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * Plays a slowly increasing and releasing sine wave
  */
case class TinnitusExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  var oscilOpt = Option.empty[NodeSourceOscil]
  var gainCtrlOpt = Option.empty[NodeControlEnvelope]

  def title = "tinnitus"

  def start(nineth: Nineth): Unit = {

    val (freq, attackRelease) = SoundUtil.xyParams(List(300.0, 401.0, 802.0), List(1.0, 2.0, 4.0))(nineth)

    // Create nodes
    val freqCtrl = ctx.createNodeControlConstant(freq)
    val gainCtrl = ctx.createNodeControlAdsr(attackRelease, 0.0, 1.0, attackRelease, 0.5)

    val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
    val gain = ctx.createNodeThroughGain
    val lineOut = ctx.createNodeSinkLineOut

    // Connect nodes
    freqCtrl >- oscil.frequency
    gainCtrl >- gain.gain

    oscil >- gain >- lineOut

    // Start nodes
    val now = ctx.currentTime
    oscil.start(0.0)
    gainCtrl.start(now)

    oscilOpt = Some(oscil)
    gainCtrlOpt = Some(gainCtrl)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    gainCtrlOpt.foreach(_.stop(now))
    oscilOpt.foreach(_.stop(now + 10))
  }

}
