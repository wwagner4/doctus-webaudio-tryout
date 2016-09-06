// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._

import scala.util.Random

/**
 * Sound using a reusable ADSR Curve
 */
case class AdsrTryout(ctx: DoctusSoundAudioContext) {

  val freqs = List(111, 222, 333, 444, 555)
  val ran = Random

  var oscilOpt = Option.empty[NodeSourceOscil]
  var adsrOpt = Option.empty[NodeControlEnvelope]

  def start(nineth: Nineth): Unit = {

    val freq = freqs(ran.nextInt(freqs.size))

    val freqCtrl = ctx.createNodeControlConstant(freq)
    val adsrCtrl = createAdsrCtrl(nineth)

    val gain = ctx.createNodeFilterGain
    val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)

    adsrCtrl >- gain.gain
    freqCtrl >- oscil.frequency
    oscil >- gain >- ctx.createNodeSinkLineOut

    val now = ctx.currentTime
    oscil.start(now)
    adsrCtrl.start(now)

    oscilOpt = Some(oscil)
    adsrOpt = Some(adsrCtrl)

  }

  def stop(): Unit = {
    val now = ctx.currentTime
    adsrOpt.foreach{_.stop(now) }
    oscilOpt.foreach { _.stop(now + 10) }
  }

  def createAdsrCtrl(nineth: Nineth): NodeControlEnvelope = {

    def createAdsr(attack: Double, decay: Double, sustain: Double): NodeControlEnvelope = {
      ctx.createNodeControlAdsr(attack, decay, sustain, 2.0)
    }

    nineth match {
      case N_00 => createAdsr(0.001, 0.1, 0.3)
      case N_10 => createAdsr(0.1, 0.1, 0.3)
      case N_20 => createAdsr(0.6, 0.4, 0.3)

      case N_01 => createAdsr(0.001, 0.1, 0.1)
      case N_11 => createAdsr(0.1, 0.1, 0.1)
      case N_21 => createAdsr(0.6, 0.4, 0.1)

      case N_02 => createAdsr(0.001, 0.1, 0.01)
      case N_12 => createAdsr(0.1, 0.1, 0.01)
      case N_22 => createAdsr(0.6, 0.4, 0.01)
    }



  }

}

