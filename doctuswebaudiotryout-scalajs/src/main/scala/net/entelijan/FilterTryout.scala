// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

case class FilterTryout(ctx: AudioContext) {

  val freq = 555.0

  val oscil = ctx.createOscillator()
  oscil.`type` = "sawtooth"
  oscil.start(0)
  val adsr = NodeAdsr(ctx)
  adsr.valSustain = 1.0

  val filterAdsr = NodeAdsr(ctx)
  filterAdsr.valAttack = 0.3
  filterAdsr.valSustain = 1.0
  filterAdsr.valRelease = 0.3

  val filterLfo = ctx.createOscillator()
  filterLfo.frequency.value = 5
  filterLfo.start(0)

  val filterGain = ctx.createGain()
  filterGain.gain.value = 800.0

  val filter = ctx.createBiquadFilter()
  filter.`type` = "lowpass"
  filter.frequency.value = freq * 1.5

  oscil.connect(filter)
  filter.connect(adsr.nodeIn)
  adsr.nodeOut.connect(ctx.destination)

  filterAdsr.nodeOut.connect(filterGain)
  filterGain.connect(filter.detune)

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