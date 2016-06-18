package net.entelijan

import org.scalajs.dom.{AudioContext, OscillatorNode}

/**
  * Plays a melody
  *
  * Introduces the concept of an 'instrument'
  *
  */
case class Melody(ctx: AudioContext, now: Double) {

  val freqs = List(111, 222, 333, 444, 555, 666, 777)
  val ran = new java.util.Random()

  def start(): Unit = {
    for (t <- 0.0 to (16, 0.3)) {
      val i = ran.nextInt(freqs.size)
      if (ranBoolean(0.7)) {
        playNote(t, 0.5, MyInstrument(ctx, freqs(i)))
      }
    }
  }

  private def ranBoolean(p: Double): Boolean = {
    ran.nextDouble() < p
  }

  private def playNote(time: Double, duration: Double, inst: Instrument): Unit = {
    inst.start(now + time)
    inst.stop(now + time + duration)
  }

}

trait Instrument {

  def start(time: Double)

  def stop(time: Double)

}

case class MyInstrument(ctx: AudioContext, freq: Double) extends Instrument {

  val oscils = Osclis(ctx)
  val oscil = oscils.next()
  oscil.frequency.value = freq
  oscil.start()

  val gain = ctx.createGain()
  gain.gain.value = 0.0

  oscil.connect(gain)
  gain.connect(ctx.destination)

  override def start(time: Double): Unit = {
    gain.gain.setValueAtTime(0, time)
    gain.gain.linearRampToValueAtTime(1.0, time + 0.01)
  }

  override def stop(time: Double): Unit = {
    gain.gain.setValueAtTime(1.0, time)
    gain.gain.linearRampToValueAtTime(0.0, time + 1.0)
  }

  case class Osclis(ctx: AudioContext) {

    private val oscils = List.fill(10)(ctx.createOscillator())
    private var idx = 0

    def next(): OscillatorNode = {
      idx = (idx + 1) % oscils.size
      oscils(idx)
    }

  }}

