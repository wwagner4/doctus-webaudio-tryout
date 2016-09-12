// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound.{DoctusSoundAudioContext, WaveType_Sawtooth}

/**
 * Plays a melody
 * Introduces the concept of an 'instrument'
 */
case class Melody(ctx: DoctusSoundAudioContext) {

  val ran = new java.util.Random()
  val freqs0 = List(222.0 * 0.995, 333.0, 444.0, 555.0 * 0.995)

  def start(): Unit = {
    val off = 0.3 + ran.nextDouble()
    val freqs = freqs0.map( _ * off)
    val startTime = ctx.currentTime
    for (time <- 0.0 to(5, 0.25)) {
      val i = ran.nextInt(freqs.size)
      if (ranBoolean(0.8)) {
        playNote(startTime, time, 0.5, MyInstrument(ctx, freqs(i)))
      }
    }
    for (time <- 2.0 to(8, 0.25)) {
      val i = ran.nextInt(freqs.size)
      if (ranBoolean(0.3)) {
        playNote(startTime + 0.01, time, 0.5, MyInstrument(ctx, freqs(i)))
      }
    }
  }

  private def ranBoolean(p: Double): Boolean = {
    ran.nextDouble() < p
  }

  private def playNote(startTime: Double, time: Double, duration: Double, inst: Instrument): Unit = {
    inst.start(startTime + time)
    inst.stop(startTime + time + duration)
  }

}

trait Instrument {

  def start(time: Double)

  def stop(time: Double)

}

case class MyInstrument(ctx: DoctusSoundAudioContext, freq: Double) extends Instrument {

  val freqCtrl = ctx.createNodeControlConstant(freq)
  val adsrCtrl = ctx.createNodeControlAdsr(0.001, 0.1, 0.2, 3.0)

  val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)
  val gain = ctx.createNodeFilterGain
  val sink = ctx.createNodeSinkLineOut

  freqCtrl >- oscil.frequency
  adsrCtrl >- gain.gain

  oscil >- gain >- sink

  oscil.start(0.0)

  def start(time: Double): Unit = {
    adsrCtrl.start(time)
  }

  def stop(time: Double): Unit = {
    adsrCtrl.stop(time)
    oscil.stop(time + 5.0)
  }

}

