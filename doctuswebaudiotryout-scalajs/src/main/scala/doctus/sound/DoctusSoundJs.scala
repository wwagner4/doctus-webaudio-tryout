package doctus.sound

/**
  * ScalaJS implementation of the (experimental sound interface)
  */
class DoctusSoundJs extends DoctusSound {

  import org.scalajs.dom.AudioContext

  val ctx = new AudioContext

  val oscil = ctx.createOscillator()
  oscil.frequency.value = 222
  oscil.start()

  val gain = ctx.createGain()
  val t1 = ctx.currentTime
  gain.gain.setValueAtTime(0, t1)

  oscil.connect(gain)
  gain.connect(ctx.destination)


  override def tinitusStart: Unit = {
    println("start")
    val t = ctx.currentTime
    gain.gain.cancelScheduledValues(t)
    gain.gain.setValueAtTime(gain.gain.value, t)
    gain.gain.linearRampToValueAtTime(0.1, t + 2)
  }

  override def tinitusStop: Unit = {
    println("stop")
    val t = ctx.currentTime
    gain.gain.cancelScheduledValues(t)
    gain.gain.setValueAtTime(gain.gain.value, t)
    gain.gain.linearRampToValueAtTime(0.0, t + 2)
  }

  override def melodyStart: Unit = {
    println("melodyStart NOT IMPLEMENTED")
  }
}
