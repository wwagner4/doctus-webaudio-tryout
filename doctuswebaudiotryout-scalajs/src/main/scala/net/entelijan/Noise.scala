package net.entelijan

import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, AudioNode}

import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: AudioContext) {

  val noiseBuffer = WebAudioUtil.createBufferNoise(ctx, 1.0, Random)

  val tremolo = WebAudioUtil.createTremolo(ctx, 0.5, 0.6, 0.3)
  tremolo.out.connect(ctx.destination)
  tremolo.start(0)

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double): Unit = {
    val noiseNode = WebAudioUtil.createBufferSourceLooping(ctx, noiseBuffer)
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

trait CustomNode {

  def in: AudioNode

  def out: AudioNode

  def start(time: Double): Unit

  def stop(time: Double): Unit

}

object WebAudioUtil {

  def createTremolo(ctx: AudioContext, frequency: Double, gain: Double, amplitude: Double): CustomNode = {

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

  def createBufferSine(ctx: AudioContext, frequency: Double, amplitude: Double, offset: Double): AudioBuffer = {
    val bufferSize = ctx.sampleRate.toInt
    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    for (i <- 0 until bufferSize) {
      val v = offset + amplitude * math.sin(i.toDouble * 2 * math.Pi * frequency / bufferSize)
      channel.set(i, v.toFloat)
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


