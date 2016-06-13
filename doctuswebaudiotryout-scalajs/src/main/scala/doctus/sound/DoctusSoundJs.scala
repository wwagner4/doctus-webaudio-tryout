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

  override def noteOn: Unit = {
    println("noteOn")
    oscil.connect(ctx.destination)
    oscil.start()
  }

  override def noteOff: Unit = {
    println("noteOff")
    oscil.stop()
    oscil.disconnect(ctx.destination)
  }

}
