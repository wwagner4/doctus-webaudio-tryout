// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound.DoctusSoundAudioContext

/**
  * Plays a slowly increasing and releasing sine wave
  */
case class Tinitus(ctx: DoctusSoundAudioContext) {

  // Init nodes
  val freq = ctx.createNodeControlConstant(333.0)

  val oscil = ctx.createNodeSourceOscilSine
  freq >- oscil.frequency

  val gainValue = ctx.createNodeControlAdsr(2.0, 0.0, 1.0, 2.0)

  val gain = ctx.createNodeFilterGain(0.0)
  gainValue >- gain.gain

  // Connect nodes
  oscil >- gain >- ctx.createNodeSinkLineOut

  // Start nodes
  oscil.start(0.0)

  def start(): Unit = {
    val t = ctx.currentTime
    gainValue.start(t)
  }

  def stop(): Unit = {
    val t = ctx.currentTime
    gainValue.stop(t)
  }

}
