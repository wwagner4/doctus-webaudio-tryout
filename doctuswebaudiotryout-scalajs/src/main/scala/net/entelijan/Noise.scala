package net.entelijan

import org.scalajs.dom.raw.{AudioNode, AudioParam}
import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, AudioNode}

import scala.scalajs.js
import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext) {

  val buffer = WebAudioUtil.createBufferNoise(ctx, Random)
  val gain = ctx.createGain()
  gain.gain.value = 1
  val gainMod = WebAudioUtil.createNodeLFO(ctx, 10, 0.5, 3)
  gainMod.start(0)
  gainMod.output.connect(gain.gain)
  gain.connect(ctx.destination)

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double): Unit = {
    val bufferSrc = WebAudioUtil.createBufferSourceLooping(ctx, buffer)
    bufferSrc.connect(gain)
    bufferSrc.start()
    bufferSrcOpt = Some(bufferSrc)
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
    val oscil = ctx.createOscillator()
    oscil.frequency.value = frequency

    val gain = ctx.createGain()
    gain.gain.value = amplitude

    val offsetBuffer = createBufferConstant(ctx, offset)
    val offsetNode = createBufferSourceLooping(ctx, offsetBuffer)

    val mixer = ctx.createChannelMerger(2)

    oscil.connect(gain)

    offsetNode.connect(mixer)
    gain.connect(mixer)

    new CustomSourceNode {

      override def output: AudioNode = mixer

      override def stop(time: Double): Unit = {
        oscil.stop(time)
      }

      override def start(time: Double): Unit = {
        oscil.start(time)
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


