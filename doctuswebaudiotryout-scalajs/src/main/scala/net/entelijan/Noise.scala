package net.entelijan

import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, AudioNode}

import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext) {

  val noiseBuffer = WebAudioUtil.createBufferNoise(ctx, Random)
  val noiseGain = ctx.createGain()
  val noiseGainMod = WebAudioUtil.createNodeLFO(ctx, 10, 0.3, 0.0)
  noiseGainMod.start(0)
  noiseGainMod.output.connect(noiseGain.gain)
  noiseGain.connect(ctx.destination)

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double): Unit = {
    val noiseNode = WebAudioUtil.createBufferSourceLooping(ctx, noiseBuffer)
    noiseNode.connect(noiseGain)
    noiseNode.start()
    bufferSrcOpt = Some(noiseNode)
  }

  def stop(time: Double): Unit = {
    bufferSrcOpt.foreach { bufferSrc =>
      bufferSrc.stop()
    }
  }

}

trait CustomSourceNode {

  def output: AudioNode

  def start(time: Double): Unit

  def stop(time: Double): Unit

}

object WebAudioUtil {

  def createBufferNoise(ctx: AudioContext, ran: Random): AudioBuffer = {
    val bufferSize = 2 * ctx.sampleRate.toInt
    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    for (i <- 0 until bufferSize) {
      val v = ran.nextFloat() * 2 - 1
      channel.set(i, v)
    }
    buffer
  }

  def createBufferConstant(ctx: AudioContext, value: Double): AudioBuffer = {
    val bufferSize = 1
    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    for (i <- 0 until bufferSize) {
      channel.set(i, value.toFloat)
    }
    buffer
  }

  def createNodeLFO(ctx: AudioContext, frequency: Double, amplitude: Double, offset: Double): CustomSourceNode = {
    val lfo = ctx.createOscillator()
    lfo.frequency.value = frequency

    val lfoGain = ctx.createGain()
    lfoGain.gain.value = amplitude

    val offsetBuffer = createBufferConstant(ctx, offset)
    val offsetNode = createBufferSourceLooping(ctx, offsetBuffer)

    val mixer = ctx.createChannelMerger()

    lfo.connect(lfoGain)

    offsetNode.connect(mixer)
    lfoGain.connect(mixer)

    new CustomSourceNode {

      override def output: AudioNode = mixer

      override def start(time: Double): Unit = {
        lfo.start(time)
      }

      override def stop(time: Double): Unit = {
        lfo.stop(time)
      }

    }

  }

  def createBufferSourceLooping(ctx: AudioContext, buffer: AudioBuffer): AudioBufferSourceNode = {
    val bufferSrc = ctx.createBufferSource()
    bufferSrc.buffer = buffer
    bufferSrc.loop = true
    bufferSrc
  }


}


