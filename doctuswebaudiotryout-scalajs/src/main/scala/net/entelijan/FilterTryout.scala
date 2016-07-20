// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

case class FilterTryout(ctx: AudioContext) {


  val oscil = ctx.createOscillator()
  oscil.`type` = "sawtooth"
  oscil.start(0)


  val adsr = NodeAdsr(ctx)
  adsr.valSustain = 1.0
  adsr.valAttack = 0.001
  adsr.valRelease = 2.0

  val filterAdsr = NodeAdsrSrc()
  filterAdsr.valAttack = 0.3
  filterAdsr.valSustain = 0.1
  filterAdsr.valDecay = 1.4
  filterAdsr.valRelease = 1.8
  filterAdsr.valGain = 10000

  val filter = ctx.createBiquadFilter()
  filter.`type` = "lowpass"

  filterAdsr.connect(filter.detune)

  oscil.connect(filter)
  filter.connect(adsr.nodeIn)
  adsr.nodeOut.connect(ctx.destination)

  def start(): Unit = {
    val t = ctx.currentTime

    val freq = 300.0
    val freqCutoff = freq * 1.5
    oscil.frequency.setValueAtTime(t, freq)
    filter.frequency.setValueAtTime(t, freqCutoff)


    adsr.start(t)
    filterAdsr.start(t)
  }

  def stop(): Unit = {
    val t = ctx.currentTime
    adsr.stop(t)
    filterAdsr.stop(t)
  }

}
