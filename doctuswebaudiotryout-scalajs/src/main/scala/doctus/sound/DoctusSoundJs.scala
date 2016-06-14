package doctus.sound

import java.time.Period

/**
  * ScalaJS implementation of the (experimental sound interface)
  */
class DoctusSoundJs extends DoctusSound {

  import org.scalajs.dom.{AudioBuffer, Event, XMLHttpRequest, GainNode, AudioContext}

  val ctx = new AudioContext

  val oscil = ctx.createOscillator()
  oscil.frequency.value = 444
  oscil.start()

  val gain = ctx.createGain()
  gain.gain.value = 0.0

  oscil.connect(gain)
  gain.connect(ctx.destination)


  override def oscilOn: Unit = {
    println("oscilOn")
    val t = ctx.currentTime
    gain.gain.linearRampToValueAtTime(1.0, t + 1.0)
  }

  override def oscilOff: Unit = {
    println("oscilOff")
    val t = ctx.currentTime
    gain.gain.linearRampToValueAtTime(0.0, t + 1.0)
  }

}
