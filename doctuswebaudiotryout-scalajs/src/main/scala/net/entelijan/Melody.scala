package net.entelijan

import org.scalajs.dom.AudioContext

/**
  * Plays a melody
  *
  * Introduces the concept of an 'instrument' and an adsr envelope
  *
  */
case class Melody(ctx: AudioContext, now: Double) {

  def start(): Unit = {
    playNote(0, 2, MyInstrument(ctx, 444))
    playNote(1, 2, MyInstrument(ctx, 555))
    playNote(3, 2, MyInstrument(ctx, 222))
  }

  private def playNote(time: Double, duration: Double, inst: Instrument): Unit = {
    inst.start(now + time)
    inst.start(now + time + duration)
  }

}

trait Instrument {

  def start(time: Double)

  def stop(time: Double)

}

case class MyInstrument(ctx: AudioContext, freq: Double) extends Instrument {

  val oscil = ctx.createOscillator()
  oscil.frequency.value = freq
  oscil.start()

  val gain = ctx.createGain()
  gain.gain.value = 0.0

  oscil.connect(gain)
  gain.connect(ctx.destination)

  override def start(time: Double): Unit = {
    gain.gain.setValueAtTime(0, time)
    gain.gain.linearRampToValueAtTime(1.0, 2.0)
  }

  override def stop(time: Double): Unit = {
    gain.gain.setValueAtTime(1.0, time)
    gain.gain.linearRampToValueAtTime(0.0, time + 2.0)
  }
}