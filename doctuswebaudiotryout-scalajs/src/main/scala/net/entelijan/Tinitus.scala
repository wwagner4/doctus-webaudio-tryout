package net.entelijan

import org.scalajs.dom.AudioContext

/**
  * Plays a slowly increasing and releasing sine wave
  */
case class Tinitus(ctx: AudioContext) {

  val maxGain = 0.2

  val oscil = ctx.createOscillator()
  oscil.frequency.value = 555
  oscil.start()

  val gain = ctx.createGain()
  val t1 = ctx.currentTime
  gain.gain.value = 0

  oscil.connect(gain)
  gain.connect(ctx.destination)

  def start: Unit = {
    println("start")
    val t = ctx.currentTime
    gain.gain.cancelScheduledValues(t)
    gain.gain.setValueAtTime(gain.gain.value, t)
    gain.gain.linearRampToValueAtTime(maxGain, t + 2)
  }

  def stop: Unit = {
    println("stop")
    val t = ctx.currentTime
    gain.gain.cancelScheduledValues(t)
    gain.gain.linearRampToValueAtTime(0.0, t + 2)
  }





}
