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
  tremolo.out.connect(ctx.destination)
  tremolo.start(0)

  var bufferSrcOpt = Option.empty[AudioBufferSourceNode]

  def start(time: Double, nineth: Nineth): Unit = {

    def setTremolo(f: Double, a: Double): Unit = {
        tremolo.frequency = f
        tremolo.amplitude = a
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

case class Tremolo(ctx: AudioContext) extends CustomNode {
  
    private val oscil = ctx.createOscillator()
    private val amplGain = ctx.createGain()
    private val inOutGain = ctx.createGain()
    
    def frequency_= (value:Double):Unit = oscil.frequency.value = value 
    def frequency = oscil.frequency.value 

    def amplitude_= (value:Double):Unit = amplGain.gain.value = value 
    def amplitude = amplGain.gain.value 

    def amplitudeOffset_= (value:Double):Unit = inOutGain.gain.value = value 
    def amplitudeOffset = inOutGain.gain.value 

    // Amplitude
    amplGain.gain.value = 0.1
    // Offset Amplitude
    inOutGain.gain.value = 0.5
    oscil.frequency.value = 0.5

    oscil.connect(amplGain)
    // The output of the oscil is added to the value previously set by amplGain.gain.value
    amplGain.connect(inOutGain.gain)

      def start(time: Double): Unit = {
        oscil.start(time)
      }

      def stop(time: Double): Unit = {
        oscil.stop(time)
      }

      override def in: AudioNode = inOutGain

      override def out: AudioNode = inOutGain
}


