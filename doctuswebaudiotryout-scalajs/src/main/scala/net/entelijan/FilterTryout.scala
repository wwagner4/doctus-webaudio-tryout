// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

import scala.util.Random

/**
  * A Filter that changes its cutoff frequency controlled by an ADSR envelope
  */
case class FilterTryout(ctx: AudioContext) {

  val ran = Random


  val oscil = ctx.createOscillator()
  oscil.`type` = "sawtooth"
  oscil.start(0)


  val adsr = NodeAdsr(ctx)
  adsr.valAttack = 0.001
  adsr.valSustain = 0.8
  adsr.valRelease = 2.0

  val filterAdsr = NodeAdsrSrc()
  filterAdsr.valAttack = 0.6
  filterAdsr.valSustain = 1.0
  filterAdsr.valRelease = 1.5
  filterAdsr.valGain = 10000

  val filter = ctx.createBiquadFilter()
  filter.`type` = "lowpass"

  filterAdsr.connect(filter.detune)

  oscil.connect(filter)
  filter.connect(adsr.nodeIn)
  adsr.nodeOut.connect(ctx.destination)

  def start(): Unit = {
    val t = ctx.currentTime

    val freq = 100 + ran.nextDouble() * 500
    oscil.frequency.setValueAtTime(freq, t)
    filter.frequency.setValueAtTime(freq * 1.5, t)

    adsr.start(t)
    filterAdsr.start(t)
  }

  def stop(): Unit = {
    val t = ctx.currentTime
    adsr.stop(t)
    filterAdsr.stop(t)
  }

}
