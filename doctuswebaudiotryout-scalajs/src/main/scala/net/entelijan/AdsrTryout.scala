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

case class Adsr(ctx: AudioContext) extends CustomNode {

  var attack = 0.01
  var decay = 0.1
  var sustain = 0.01
  var release = 0.5

  val gain = ctx.createGain()
  gain.gain.setValueAtTime(0, 0)

  override def start(time: Double): Unit = {
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(0, time)
    gain.gain.linearRampToValueAtTime(1.0, time + attack)
    gain.gain.linearRampToValueAtTime(sustain, time + attack + decay)
  }

  override def stop(time: Double): Unit = {
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(gain.gain.value, time)
    gain.gain.linearRampToValueAtTime(0.0, time + release)
  }

  override def in: AudioNode = gain

  override def out: AudioNode = gain

}

