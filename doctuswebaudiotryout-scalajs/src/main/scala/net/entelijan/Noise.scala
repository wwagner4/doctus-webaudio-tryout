// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: DoctusSoundAudioContext, noiseType: NoiseType) {

  val ran = Random

  var noiseOpt = Option.empty[StartStoppable]

  def start(time: Double, nineth: Nineth): Unit = {

    def createLfo(f: Double, a: Double): NodeControlLfo = {
      ctx.createNodeControlLfo(WaveType_Sine, f, a)
    }

    val lfoCtrl = nineth match {
      case N_00 => createLfo(5.0, 0.2)
      case N_01 => createLfo(1.0, 0.2)
      case N_02 => createLfo(0.5, 0.2)

      case N_10 => createLfo(5.0, 0.9)
      case N_11 => createLfo(1.0, 0.9)
      case N_12 => createLfo(0.5, 0.9)

      case N_20 => createLfo(5.0, 1.4)
      case N_21 => createLfo(1.0, 1.4)
      case N_22 => createLfo(0.5, 1.4)
    }

    val constCtrl = ctx.createNodeControlConstant(1.5)

    val noise = ctx.createNodeSourceNoise(noiseType)
    val gain = ctx.createNodeThroughGain
    val out = ctx.createNodeSinkLineOut

    lfoCtrl >- gain.gain
    constCtrl >- gain.gain

    noise >- gain >- out

    lfoCtrl.start(0.0)
    noise.start(0.0)

    noiseOpt = Some(noise)
  }

  def stop(time: Double): Unit = {
    val t = ctx.currentTime
    noiseOpt.foreach { bufferSrc =>
      bufferSrc.stop(t)
    }
  }

}
