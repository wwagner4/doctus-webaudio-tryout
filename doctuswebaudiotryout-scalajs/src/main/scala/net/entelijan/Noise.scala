package net.entelijan

import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, AudioNode}

import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext) {

  val ran = Random

  val buffer = WebAudioUtil.createBufferNoise(ctx, ran, 0.3)

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double): Unit = {
    val bufferSrc = WebAudioUtil.createBufferSourceLooping(ctx, buffer)
    bufferSrc.connect(ctx.destination)
    bufferSrc.start()
    bufferSrcOpt = Some(bufferSrc)
  }

  def stop(time: Double): Unit = {
    bufferSrcOpt.foreach { bufferSrc =>
      bufferSrc.disconnect(ctx.destination)
      bufferSrc.stop()
    }
  }

}

object WebAudioUtil {

  def createBufferNoise(ctx: AudioContext, ran: Random, gain: Double): AudioBuffer = {
    val bufferSize = 2 * ctx.sampleRate.toInt
    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    for (i <- 0 until bufferSize) {
      val v = (ran.nextFloat() * 2 - 1) * gain.toFloat
      channel.set(i, v)
    }
    buffer
  }

  def createBufferSourceLooping(ctx: AudioContext, buffer: AudioBuffer): AudioBufferSourceNode = {
    val bufferSrc = ctx.createBufferSource()
    bufferSrc.buffer = buffer
    bufferSrc.loop = true
    bufferSrc
  }


}


