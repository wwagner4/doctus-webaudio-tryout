// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan.experiments

import doctus.sound.{DoctusSoundAudioContext, WaveType_Sawtooth}
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * Plays one of three melodies
  * Introduces the concept of an 'instrument'
  */
case class MelodyExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "melody"

  private val freqsBase = List(222.0, 333.0, 444.0, 555.0 * 0.95)
  private val freqOffsets = List(1.0, 3.0 / 4.0, 1.0 / 2.0)
  private val timeUnit = 0.3

  def start(nineth: Nineth): Unit = {

    val (melodyIndex, freqOffset) = SoundUtil.xyParams(List(0, 1, 2), freqOffsets)(nineth)

    val freqs = freqsBase.map(_ * freqOffset)

    val now = ctx.currentTime
    val startTime = now + timeUnit - now % timeUnit

    melodyIndex match {
      case 0 =>
        playNote(startTime + timeUnit * 0, 0.1, MyInstrument(freqs(0))(ctx))
        playNote(startTime + timeUnit * 1, 0.2, MyInstrument(freqs(0))(ctx))
        playNote(startTime + timeUnit * 2, 0.3, MyInstrument(freqs(2))(ctx))
        playNote(startTime + timeUnit * 3, 1.0, MyInstrument(freqs(3))(ctx))

        playNote(startTime + timeUnit * 5, 1.0, MyInstrument(freqs(3))(ctx))
        playNote(startTime + timeUnit * 7, 0.3, MyInstrument(freqs(1))(ctx))
        playNote(startTime + timeUnit * 8, 1.0, MyInstrument(freqs(2))(ctx))

      case 1 =>
        playNote(startTime + timeUnit * 0, 0.1, MyInstrument(freqs(0))(ctx))
        playNote(startTime + timeUnit * 1, 0.2, MyInstrument(freqs(0))(ctx))
        playNote(startTime + timeUnit * 2, 0.3, MyInstrument(freqs(2))(ctx))
        playNote(startTime + timeUnit * 3, 1.0, MyInstrument(freqs(0))(ctx))

        playNote(startTime + timeUnit * 5, 1.0, MyInstrument(freqs(0))(ctx))
        playNote(startTime + timeUnit * 7, 0.3, MyInstrument(freqs(1))(ctx))
        playNote(startTime + timeUnit * 8, 1.0, MyInstrument(freqs(2))(ctx))

      case 2 =>
        playNote(startTime + timeUnit * 0, 0.1, MyInstrument(freqs(3))(ctx))
        playNote(startTime + timeUnit * 1, 0.2, MyInstrument(freqs(3))(ctx))
        playNote(startTime + timeUnit * 2, 0.3, MyInstrument(freqs(2))(ctx))
        playNote(startTime + timeUnit * 3, 1.0, MyInstrument(freqs(1))(ctx))

        playNote(startTime + timeUnit * 5, 1.0, MyInstrument(freqs(0))(ctx))
        playNote(startTime + timeUnit * 7, 0.3, MyInstrument(freqs(3))(ctx))
        playNote(startTime + timeUnit * 8, 1.0, MyInstrument(freqs(3))(ctx))

    }
  }

  def stop(): Unit = {} // Nothing to do

  private def playNote(time: Double, duration: Double, inst: Instrument): Unit = {
    inst.start(time)
    inst.stop(time + duration)
  }

  trait Instrument {

    def start(time: Double)

    def stop(time: Double)

  }

  case class MyInstrument(freq: Double)(implicit ctx: DoctusSoundAudioContext) extends Instrument {

    val freqCtrl = ctx.createNodeControlConstant(freq)
    val adsrCtrl = ctx.createNodeControlAdsr(0.001, 0.4, 0.4, 2.0, 0.1)

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

}

