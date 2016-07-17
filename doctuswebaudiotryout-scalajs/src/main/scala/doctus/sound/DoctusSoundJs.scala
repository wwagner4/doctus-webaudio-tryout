// Copyright (C) 2016 wolfgang wagner http://entelijan.net

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

  def tinitusStart(): Unit = tinitus.start

  def tinitusStop(): Unit = tinitus.stop

  def noiseWhiteStart(nineth: Nineth): Unit = noiseWhite.start(ctx.currentTime, nineth)

  def noiseWhiteStop(): Unit = noiseWhite.stop(ctx.currentTime)

  def noisePinkStart(nineth: Nineth): Unit = noisePink.start(ctx.currentTime, nineth)

  def noisePinkStop(): Unit = noisePink.stop(ctx.currentTime)

  def noiseBrownRedStart(nineth: Nineth): Unit = noiseBrownRed.start(ctx.currentTime, nineth)

  def noiseBrownRedStop(): Unit = noiseBrownRed.stop(ctx.currentTime)

  def melodyStart(): Unit = Melody(ctx, ctx.currentTime).start()

  def adsrStart(nineth: Nineth): Unit = adsrTryout.start(nineth)

  def adsrStop(): Unit = adsrTryout.stop()

  def metalStart(): Unit = metalTryout.start()

  def metalStop(): Unit = metalTryout.stop()
  
  def filterStart(): Unit = ???

  def filterStop(): Unit = ???

}

sealed trait NoiseType

case object NT_White extends NoiseType

case object NT_Pink extends NoiseType

case object NT_Red extends NoiseType

case object NT_Brown extends NoiseType

