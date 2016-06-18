package net.entelijan

import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext}

import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext) {

  val ran = Random

  val buffer = createBuffer

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double): Unit = {
    val bufferSrc = ctx.createBufferSource()
    bufferSrc.buffer = buffer
    bufferSrc.loop = true
    bufferSrc.connect(ctx.destination)
    bufferSrc.start()
    bufferSrcOpt = Some(bufferSrc)
  }

  def stop(time: Double): Unit = {
    bufferSrcOpt.foreach { bufferSrc =>
      bufferSrc.connect(ctx.destination)
      bufferSrc.stop()
    }
  }

  private def createBuffer: AudioBuffer = {
    val ampl = 0.5f
    val bufferSize = 2 * ctx.sampleRate.toInt
    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    for (i <- 0 until bufferSize) {
      val v = ran.nextFloat() * 2 * ampl - ampl
      channel.set(i, v)
    }
    buffer
  }

}


