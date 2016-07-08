package net.entelijan

import doctus.sound._
import org.scalajs.dom.{AudioContext, AudioNode}

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
  adsr.release = 2.0

  oscil.connect(adsr.in)
  adsr.out.connect(ctx.destination)

  oscil.start()

  def start(nineth: Nineth): Unit = {
    val i = ran.nextInt(freqs.size)
    oscil.frequency.value = freqs(i)
    nineth match {
      case N_00 =>
        adsr.attack = 0.001
        adsr.sustain = 0.3
      case N_10 =>
        adsr.attack = 0.01
        adsr.sustain = 0.3
      case N_20 =>
        adsr.attack = 0.6
        adsr.sustain = 0.3

      case N_01 =>
        adsr.attack = 0.001
        adsr.sustain = 0.1
      case N_11 =>
        adsr.attack = 0.01
        adsr.sustain = 0.1
      case N_21 =>
        adsr.attack = 0.6
        adsr.sustain = 0.1

      case N_02 =>
        adsr.attack = 0.001
        adsr.sustain = 0.01
      case N_12 =>
        adsr.attack = 0.01
        adsr.sustain = 0.01
      case N_22 =>
        adsr.attack = 0.6
        adsr.sustain = 0.01
    }
    adsr.start(ctx.currentTime)
  }

  def stop(): Unit = {
    adsr.stop(ctx.currentTime)
  }

}

