package doctus.sound

import net.entelijan._

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
  val adsrTryout = AdsrTryout(ctx)
  val metalTryout = MetalTryout(ctx)

  override def tinitusStart(): Unit = tinitus.start

  override def tinitusStop(): Unit = tinitus.stop

  override def noiseWhiteStart(nineth: Nineth): Unit = noiseWhite.start(ctx.currentTime, nineth)

  override def noiseWhiteStop(): Unit = noiseWhite.stop(ctx.currentTime)

  override def noisePinkStart(nineth: Nineth): Unit = noisePink.start(ctx.currentTime, nineth)

  override def noisePinkStop(): Unit = noisePink.stop(ctx.currentTime)

  override def noiseBrownRedStart(nineth: Nineth): Unit = noiseBrownRed.start(ctx.currentTime, nineth)

  override def noiseBrownRedStop(): Unit = noiseBrownRed.stop(ctx.currentTime)

  override def melodyStart(): Unit = Melody(ctx, ctx.currentTime).start()

  override def adsrStart(nineth: Nineth): Unit = adsrTryout.start(nineth)

  override def adsrStop(): Unit = adsrTryout.stop()
  
  def metalStart(): Unit = metalTryout.start()
  
  def metalStop(): Unit = metalTryout.stop()

}

sealed trait NoiseType

case object NT_White extends NoiseType

case object NT_Pink extends NoiseType

case object NT_Red extends NoiseType

case object NT_Brown extends NoiseType

