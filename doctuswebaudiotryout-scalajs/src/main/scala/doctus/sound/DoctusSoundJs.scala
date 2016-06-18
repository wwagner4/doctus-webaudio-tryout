package doctus.sound

import net.entelijan.{Melody, Noise, Tinitus}

/**
  * ScalaJS implementation of the (experimental sound interface)
  */
class DoctusSoundJs extends DoctusSound {

  import org.scalajs.dom.AudioContext

  val ctx = new AudioContext

  val tinitus = Tinitus(ctx)

  val noise = Noise(ctx)

  override def tinitusStart: Unit = tinitus.start

  override def tinitusStop: Unit = tinitus.stop

  override def noiseStart: Unit = noise.start(ctx.currentTime)

  override def noiseStop: Unit = noise.stop(ctx.currentTime)

  override def melodyStart: Unit = Melody(ctx, ctx.currentTime).start()

}
