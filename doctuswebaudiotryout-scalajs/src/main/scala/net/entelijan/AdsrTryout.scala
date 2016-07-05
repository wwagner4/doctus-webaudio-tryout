package net.entelijan
import doctus.sound.Nineth
import org.scalajs.dom.{AudioContext, AudioNode}

/**
  * Sound using a reusable ADSR Curve
  */
case class AdsrTryout(ctx: AudioContext) {

  val oscil = ctx.createOscillator()
  oscil.`type` = "sawtooth"
  oscil.frequency.value = 222f

  val adsr = Adsr(ctx)

  oscil.connect(adsr.in)
  adsr.out.connect(ctx.destination)

  oscil.start()

  def start(nineth: Nineth): Unit = {
    // TODO Use nineth here to control the adsr
    adsr.start(ctx.currentTime)
  }

  def stop(): Unit = {
    adsr.stop(ctx.currentTime)
  }

}

case class Adsr(ctx: AudioContext) extends CustomNode {

  val attack = 0.01
  val decay = 0.1
  val sustain = 0.01
  val release = 0.5

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

