// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan.experiments

import doctus.sound._
import net.entelijan._

import scala.util.Random

/**
  * Sound using a reusable ADSR Curve
  */
case class AdsrExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title = "ADSR"

  val freqs = List(111, 222, 333, 444, 555)
  val ran = Random

  var oscilOpt = Option.empty[NodeSourceOscil]
  var adsrOpt = Option.empty[StartStoppable]

  def start(nineth: Nineth): Unit = {

    val freq = freqs(ran.nextInt(freqs.size))

    val freqCtrl = ctx.createNodeControlConstant(freq)

    val gain = createAdsrCtrl(nineth)
    val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)

    freqCtrl >- oscil.frequency
    oscil >- gain >- ctx.createNodeSinkLineOut

    val now = ctx.currentTime
    oscil.start(now)
    gain.start(now)

    oscilOpt = Some(oscil)
    adsrOpt = Some(gain)

  }

  def stop(): Unit = {
    val now = ctx.currentTime
    adsrOpt.foreach {
      _.stop(now)
    }
    oscilOpt.foreach {
      _.stop(now + 10)
    }
  }

  def createAdsrCtrl(nineth: Nineth): NodeThrough with StartStoppable = {

    val r = 1.3

    nineth match {
      case N_00 => createAdsr(0.001, 0.1, 0.3, r)
      case N_10 => createAdsr(0.1, 0.1, 0.3, r)
      case N_20 => createAdsr(0.6, 0.1, 0.3, r)

      case N_01 => createAdsr(0.001, 0.1, 0.1, r)
      case N_11 => createAdsr(0.1, 0.1, 0.1, r)
      case N_21 => createAdsr(0.6, 0.1, 0.1, r)

      case N_02 => createAdsr(0.001, 0.1, 0.01, r)
      case N_12 => createAdsr(0.1, 0.1, 0.01, r)
      case N_22 => createAdsr(0.6, 0.1, 0.01, r)
    }


  }

  def createAdsr(a: Double, d: Double, s: Double, r: Double): NodeThrough with StartStoppable = {
    val gain = ctx.createNodeThroughGain
    val adsr = ctx.createNodeControlAdsr(a, d, s, r)

    adsr >- gain.gain

    new NodeThroughContainer with StartStoppable {

      def start(time: Double): Unit = {
        adsr.start(time)
      }

      def stop(time: Double): Unit = {
        adsr.stop(time)
      }

      def source: NodeSource = gain

      def sink: NodeSink = gain

    }

  }


}

