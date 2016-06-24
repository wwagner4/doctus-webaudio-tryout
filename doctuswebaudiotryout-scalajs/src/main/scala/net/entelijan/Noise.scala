package net.entelijan

import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, AudioNode}

import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext) {

  val noiseBuffer = WebAudioUtil.createBufferNoise(ctx, 1.0, Random)
  val noiseGain = ctx.createGain()
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

object WebAudioUtil {

  def createBufferNoise(ctx: AudioContext, amplitude: Double, ran: Random): AudioBuffer = {
    val bufferSize = ctx.sampleRate.toInt
    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    val ampl = amplitude.toFloat
    for (i <- 0 until bufferSize) {
      val v = ran.nextFloat() * ampl - ampl / 2f
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


