package doctus.sound

import net.entelijan.{Melody, Tinitus}

/**
  * ScalaJS implementation of the (experimental sound interface)
  */
class DoctusSoundJs extends DoctusSound {

  import org.scalajs.dom.AudioContext

  val ctx = new AudioContext

  val tinitus = Tinitus(ctx)

  override def tinitusStart: Unit = tinitus.start

  override def tinitusStop: Unit = tinitus.stop

  override def melodyStart: Unit = {
    Melody(ctx, ctx.currentTime).start()
  }
}
