package net.entelijan

import doctus.sound._
import org.scalajs.dom._

import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext, noiseType: NoiseType) {

  val ran = Random
  val util = WebAudioUtil(ctx, ran)

  val tremolo = Tremolo(ctx)
  tremolo.nodeOut.connect(ctx.destination)
  tremolo.start(0)

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double, nineth: Nineth): Unit = {

    def setTremolo(f: Double, a: Double): Unit = {
        tremolo.propFrequency.value = f
        tremolo.propAmplitude.value = a
    }
    
    nineth match {
      case N_00 => setTremolo(5.0, 0.1)
      case N_01 => setTremolo(1.0, 0.1)
      case N_02 => setTremolo(0.5, 0.1)

      case N_10 => setTremolo(5.0, 0.2)
      case N_11 => setTremolo(1.0, 0.2)
      case N_12 => setTremolo(0.5, 0.2)

      case N_20 => setTremolo(5.0, 0.4)
      case N_21 => setTremolo(1.0, 0.4)
      case N_22 => setTremolo(0.5, 0.4)

    }
    
    val noiseNode = util.createNodeNoise(noiseType)
    noiseNode.connect(tremolo.nodeIn)
    noiseNode.start()
    bufferSrcOpt = Some(noiseNode)
  }

  def stop(time: Double): Unit = {
    bufferSrcOpt.foreach { bufferSrc =>
      bufferSrc.stop()
    }
  }

}

case class WebAudioUtil(ctx: AudioContext, ran: Random) {

  private lazy val bufferNoiseWhite = createBufferNoise(NoiseWhite)
  private lazy val bufferNoisePink = createBufferNoise(NoisePink())
  // Brown and red noise is the same
  private lazy val bufferNoiseRed = createBufferNoise(NoiseBrown(ctx.sampleRate))
  private lazy val bufferNoiseBrown = createBufferNoise(NoiseBrown(ctx.sampleRate))

  def createNodeNoise(noiseType: NoiseType): AudioBufferSourceNode = noiseType match {
    case NT_White => createBufferSourceLooping(bufferNoiseWhite)
    case NT_Pink => createBufferSourceLooping(bufferNoisePink)
    case NT_Red => createBufferSourceLooping(bufferNoiseRed)
    case NT_Brown => createBufferSourceLooping(bufferNoiseBrown)
  }

  private def createBufferNoise(valSeq: ValueSequence): AudioBuffer = {
    val bufferSize = ctx.sampleRate.toInt * 2
    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    for (i <- 0 until bufferSize) {
      channel.set(i, valSeq.nextValue.toFloat)
    }
    buffer
  }

  private def createBufferSourceLooping(buffer: AudioBuffer): AudioBufferSourceNode = {
    val bufferSrc = ctx.createBufferSource()
    bufferSrc.buffer = buffer
    bufferSrc.loop = true
    bufferSrc
  }


}



