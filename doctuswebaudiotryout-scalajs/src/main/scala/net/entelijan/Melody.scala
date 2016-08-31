// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound.DoctusSoundAudioContext

/**
 * Plays a melody
 * Introduces the concept of an 'instrument'
 */
case class Melody(ctx: DoctusSoundAudioContext) {
  

  val freqs = List(222.0, 333.0, 444.0, 555.0, 666.0)
  val ran = new java.util.Random()

  def start(): Unit = {
    val now = ctx.currentTime
    for (t <- 0.0 to(2, 0.2)) {
      val i = ran.nextInt(freqs.size)
      if (ranBoolean(0.6)) {
        playNote(now, t, 0.2, MyInstrument(ctx, freqs(i)))
      }
    }
  }

  private def ranBoolean(p: Double): Boolean = {
    ran.nextDouble() < p
  }

  private def playNote(now: Double, time: Double, duration: Double, inst: Instrument): Unit = {
    inst.start(now + time)
    inst.stop(now + time + duration)
  }

}

trait Instrument {

  def start(time: Double)

  def stop(time: Double)

}

case class MyInstrument(ctx: DoctusSoundAudioContext, freq: Double) extends Instrument {

  val freqCtrl = ctx.createNodeControlConstant(freq)
  val adsrCtrl = ctx.createNodeControlAdsr(0.1, 0.0, 1.0, 1.0)

  val oscil = ctx.createNodeSourceOscilSawtooth
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
    val t1 = time + 1.5
    oscil.stop(t1)
  }

}

