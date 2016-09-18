// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._

/**
  * Plays a slowly increasing and releasing sine wave
  */
case class Tinnitus(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  var oscilOpt = Option.empty[NodeSourceOscil]
  var gainCtrlOpt = Option.empty[NodeControlEnvelope]

  def title = "tinnitus"

  def start(nineth: Nineth): Unit = {

    // Create nodes
    val freqCtrl = ctx.createNodeControlConstant(400.0)
    val gainCtrl = ctx.createNodeControlAdsr(5.0, 0.0, 1.0, 3.0, 0.1)

    val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
    val gain = ctx.createNodeThroughGain
    val lineOut = ctx.createNodeSinkLineOut

    // Connect nodes
    freqCtrl >- oscil.frequency
    gainCtrl >- gain.gain

    oscil >- gain >- lineOut

    // Start oscillator
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
