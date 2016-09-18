// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound.{DoctusSoundAudioContext, WaveType_Sawtooth}

/**
  * Plays a random melody melody
  * Introduces the concept of an 'instrument'
  */
case class Melody(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "melody"

  private val ran = new java.util.Random()
  private val freqsBase = List(222.0, 333.0, 444.0, 555.0)
  private val offsets = List(1.0, 3.0 / 4.0, 1.0 / 2.0, 3.0 / 4.0)

  private val freqsSize = freqsBase.size
  private var freqIndex = ran.nextInt(freqsSize)

  def start(nienth: Nineth): Unit = {

    println("melody start")

    val offset = offsets(ran.nextInt(offsets.size))
    val freqs = freqsBase.map(_ * offset)
    val startTime = ctx.currentTime

    for (time <- 0.0 to(5, 0.25)) {
      freqIndex = nextIndex(freqIndex, freqsSize)
      if (ranBoolean(0.6)) {
        playNote(startTime + time, 0.5, MyInstrument(freqs(freqIndex))(ctx))
      }
    }
  }

  def stop(): Unit = {
    println("melody stop")
  }

  private def playNote(time: Double, duration: Double, inst: Instrument): Unit = {
    inst.start(time)
    inst.stop(time + duration)
  }

  private def nextIndex(i: Int, size: Int): Int =
    if (ranBoolean(0.8)) i
    else if (i == size - 1) i - 1
    else if (i == 0) i + 1
    else if (ranBoolean(0.7)) i + 1
    else i - 1

  private def ranBoolean(p: Double): Boolean =
    ran.nextDouble() < p

}

trait Instrument {

  def start(time: Double)

  def stop(time: Double)

}

case class MyInstrument(freq: Double)(implicit ctx: DoctusSoundAudioContext) extends Instrument {

  val freqCtrl = ctx.createNodeControlConstant(freq)
  val adsrCtrl = ctx.createNodeControlAdsr(0.001, 0.1, 0.1, 3.0, 0.1)

  val oscil = ctx.createNodeSourceOscil(WaveType_Sawtooth)
  val gain = ctx.createNodeThroughGain
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

