package doctus.sound

import net.entelijan.{Melody, Noise, Tinitus}

/**
  * ScalaJS implementation of the (experimental sound interface)
  */
class DoctusSoundJs extends DoctusSound {

  import org.scalajs.dom.AudioContext

  val ctx = new AudioContext

  val tinitus = Tinitus(ctx)

  val noiseWhite = Noise(ctx, NT_White)
  val noisePink = Noise(ctx, NT_Pink)
  val noiseBrownRed = Noise(ctx, NT_Brown)

  override def tinitusStart(): Unit = tinitus.start

  override def tinitusStop(): Unit = tinitus.stop

  override def noiseWhiteStart(): Unit = noiseWhite.start(ctx.currentTime)

  override def noiseWhiteStop(): Unit = noiseWhite.stop(ctx.currentTime)

  override def noisePinkStart(): Unit = noisePink.start(ctx.currentTime)

  override def noisePinkStop(): Unit = noisePink.stop(ctx.currentTime)

  override def noiseBrownRedStart(): Unit = noiseBrownRed.start(ctx.currentTime)

  override def noiseBrownRedStop(): Unit = noiseBrownRed.stop(ctx.currentTime)

  override def melodyStart(): Unit = Melody(ctx, ctx.currentTime).start()

}


sealed trait NoiseType

case object NT_White extends NoiseType

case object NT_Pink extends NoiseType

case object NT_Red extends NoiseType

case object NT_Brown extends NoiseType

