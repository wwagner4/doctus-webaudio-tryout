// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound.DoctusSoundAudioContext

/**
  * Plays a slowly increasing and releasing sine wave
  */
case class Tinitus(ctx: DoctusSoundAudioContext) {

  // Create nodes
  val freqCtrl = ctx.createNodeControlConstant(400.0)
  val gainCtrl = ctx.createNodeControlAdsr(1.0, 0.0, 1.0, 4.0)
  val oscil = ctx.createNodeSourceOscilSine
  val gain = ctx.createNodeFilterGain(0.0)
  val lineOut = ctx.createNodeSinkLineOut

  // Connect nodes
  freqCtrl >- oscil.frequency
  gainCtrl >- gain.gain

  oscil >- gain >- lineOut

  // Start oscil
  oscil.start(0.0)

  def start(): Unit = {
    val t = ctx.currentTime
    gainCtrl.start(t)
  }

  def stop(): Unit = {
    val t = ctx.currentTime
    gainCtrl.stop(t)
  }

}
