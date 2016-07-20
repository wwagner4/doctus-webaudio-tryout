// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

case class FilterTryout(ctx: AudioContext) {

  val freq = 300.0
  val freqCutoff = 500.0

  val oscil = ctx.createOscillator()
  oscil.`type` = "sawtooth"
  oscil.start(0)
  val adsr = NodeAdsr(ctx)
  adsr.valSustain = 1.0
  adsr.valAttack = 0.001

  val filterAdsr = NodeAdsrSrc()
  filterAdsr.valAttack = 0.5
  filterAdsr.valSustain = 1.0
  filterAdsr.valRelease = 0.5
  filterAdsr.valGain = 100

  val filter = ctx.createBiquadFilter()
  filter.`type` = "lowpass"
  filter.frequency.value = freqCutoff

  filterAdsr.connect(filter.detune)

  oscil.connect(filter)
  filter.connect(adsr.nodeIn)
  adsr.nodeOut.connect(ctx.destination)

  def start(): Unit = {
    val t = ctx.currentTime
    adsr.start(t)
    filterAdsr.start(t)
  }

  def stop(): Unit = {
    val t = ctx.currentTime
    adsr.stop(t)
    filterAdsr.stop(t)
  }

}
