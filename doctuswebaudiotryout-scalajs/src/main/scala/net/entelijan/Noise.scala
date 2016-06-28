package net.entelijan

import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, AudioNode}

import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext) {

  val ran = Random
  val util = WebAudioUtil(ctx, ran)

  val tremolo = util.createTremolo(0.5, 0.2, 0.1)
  tremolo.out.connect(ctx.destination)
  tremolo.start(0)

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double): Unit = {
    val noiseNode = util.createNodeNoise(NT_Pink)
    noiseNode.connect(tremolo.in)
    noiseNode.start()
    bufferSrcOpt = Some(noiseNode)
  }

  def stop(time: Double): Unit = {
    bufferSrcOpt.foreach { bufferSrc =>
      bufferSrc.stop()
    }
  }

}

trait CustomNode extends CustomSourceNode {

  def in: AudioNode

}

trait CustomSourceNode {

  def out: AudioNode

  def start(time: Double): Unit

  def stop(time: Double): Unit

}

sealed trait NoiseType

case object NT_White extends NoiseType

case object NT_Pink extends NoiseType

case object NT_Red extends NoiseType

case object NT_Brown extends NoiseType

case class WebAudioUtil(ctx: AudioContext, ran: Random) {

  private lazy val bufferNoiseWhite = createBufferNoise(NoiseWhite)
  private lazy val bufferNoisePink = createBufferNoise(NoisePink())
  private lazy val bufferNoiseRed = createBufferNoise(NoiseWhite)
  private lazy val bufferNoiseBrown = createBufferNoise(NoiseWhite)

  def createTremolo(frequency: Double, gain: Double, amplitude: Double): CustomNode = {

    val oscil = ctx.createOscillator()
    val amplGain = ctx.createGain()
    val inOutGain = ctx.createGain()

    amplGain.gain.value = amplitude
    // Offset value
    inOutGain.gain.value = gain
    oscil.frequency.value = frequency

    oscil.connect(amplGain)
    // The output of the oscil is added to the value previously set by amplGain.gain.value
    amplGain.connect(inOutGain.gain)

    new CustomNode {
      def start(time: Double): Unit = {
        oscil.start(time)
      }

      def stop(time: Double): Unit = {
        oscil.stop(time)
      }

      override def in: AudioNode = inOutGain

      override def out: AudioNode = inOutGain
    }

  }

  def createNodeNoise(noiseType: NoiseType): AudioBufferSourceNode = noiseType match {
    case NT_White => createBufferSourceLooping(bufferNoiseWhite)
    case NT_Pink => createBufferSourceLooping(bufferNoisePink)
    case NT_Red => createBufferSourceLooping(bufferNoiseRed)
    case NT_Brown => createBufferSourceLooping(bufferNoiseBrown)
  }

  private def createBufferNoise(valSeq: ValueSequence): AudioBuffer = {
    val bufferSize = ctx.sampleRate.toInt * 4
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


