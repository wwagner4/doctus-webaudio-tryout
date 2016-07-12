// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

import scala.util.Random

/**
 * Sound using a reusable ADSR Curve
 */
case class AdsrTryout(ctx: AudioContext) {

  val freqs = List(111, 222, 333, 444, 555)
  val ran = Random

  val oscil = ctx.createOscillator()
  oscil.`type` = "sawtooth"
  oscil.frequency.value = 0f

  val adsr = Adsr(ctx)
  adsr.valRelease = 2.0
  adsr.valDecay = 0.1

  oscil.connect(adsr.nodeIn)
  adsr.nodeOut.connect(ctx.destination)

  oscil.start()

  def start(nineth: Nineth): Unit = {
    val i = ran.nextInt(freqs.size)
    oscil.frequency.value = freqs(i)
    nineth match {
      case N_00 =>
        adsr.valAttack = 0.001
        adsr.valSustain = 0.3
      case N_10 =>
        adsr.valAttack = 0.01
        adsr.valSustain = 0.3
      case N_20 =>
        adsr.valAttack = 0.6
        adsr.valDecay = 0.4
        adsr.valSustain = 0.3

      case N_01 =>
        adsr.valAttack = 0.001
        adsr.valSustain = 0.1
      case N_11 =>
        adsr.valAttack = 0.01
        adsr.valSustain = 0.1
      case N_21 =>
        adsr.valAttack = 0.6
        adsr.valDecay = 0.4
        adsr.valSustain = 0.1

      case N_02 =>
        adsr.valAttack = 0.001
        adsr.valSustain = 0.01
      case N_12 =>
        adsr.valAttack = 0.01
        adsr.valSustain = 0.01
      case N_22 =>
        adsr.valAttack = 0.6
        adsr.valDecay = 0.4
        adsr.valSustain = 0.01
    }
    adsr.start(ctx.currentTime)
  }

  def stop(): Unit = {
    adsr.stop(ctx.currentTime)
  }

}

