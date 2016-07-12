// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import org.scalajs.dom.AudioContext

/**
  * Plays a slowly increasing and releasing sine wave
  */
case class Tinitus(ctx: AudioContext) {

  val maxGain = 0.3

  val oscil = ctx.createOscillator()
  oscil.frequency.value = 333.0

  val gain = ctx.createGain()
  gain.gain.value = 0.0

  oscil.connect(gain)
  gain.connect(ctx.destination)
  oscil.start()

  def start: Unit = {
    val t = ctx.currentTime
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(gain.gain.value, t)
    gain.gain.linearRampToValueAtTime(maxGain, t + 2)
  }

  def stop: Unit = {
    val v = gain.gain.value
    val t = ctx.currentTime
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(v, t)
    gain.gain.linearRampToValueAtTime(0.0, t + 2)
  }

}
