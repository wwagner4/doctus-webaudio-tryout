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


  override def oscilOn: Unit = {
    println("oscilOn")
    oscil.connect(ctx.destination)
  }

  override def oscilOff: Unit = {
    println("oscilOff")
    oscil.disconnect(ctx.destination)
  }

}
