// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import scala.util.Random

/**
  * Creating some noise.
  */
case class Noise(ctx: DoctusSoundAudioContext, noiseType: NoiseType) {

  val ran = Random

  var noiseOpt = Option.empty[StartStoppable]

  def start(time: Double, nineth: Nineth): Unit = {

    def createLfo(f: Double, a: Double): NodeControlLfo = {
      ctx.createNodeControlLfo(f, a, 1.0)
    }

    val lfoCtrl = nineth match {
      case N_00 => createLfo(5.0, 0.1)
      case N_01 => createLfo(1.0, 0.1)
      case N_02 => createLfo(0.5, 0.1)

      case N_10 => createLfo(5.0, 0.2)
      case N_11 => createLfo(1.0, 0.2)
      case N_12 => createLfo(0.5, 0.2)

      case N_20 => createLfo(5.0, 0.4)
      case N_21 => createLfo(1.0, 0.4)
      case N_22 => createLfo(0.5, 0.4)
    }

    val noise = noiseType match {
      case NT_White => ctx.createNodeSourceNoiseWhite
      case NT_Pink => ctx.createNodeSourceNoisePink
      case NT_Red => ctx.createNodeSourceNoiseBrown
      case NT_Brown => ctx.createNodeSourceNoiseBrown
    }

    val gain = ctx.createNodeFilterGain

    lfoCtrl >- gain.gain
    noise >- gain >- ctx.createNodeSinkLineOut

    noise.start(0.0)

    noiseOpt = Some(noise)
  }

  def stop(time: Double): Unit = {
    val t = ctx.currentTime
    noiseOpt.foreach { bufferSrc =>
      bufferSrc.stop(t)
    }
  }

}

//case class WebAudioUtil(ctx: DoctusSoundAudioContext, ran: Random) {
//
//  private lazy val bufferNoiseWhite = createBufferNoise(NoiseWhite)
//  private lazy val bufferNoisePink = createBufferNoise(NoisePink())
//  // Brown and red noise is the same
//  private lazy val bufferNoiseRed = createBufferNoise(NoiseBrown(ctx.sampleRate))
//  private lazy val bufferNoiseBrown = createBufferNoise(NoiseBrown(ctx.sampleRate))
//
//  def createNodeNoise: NoiseType => AudioBufferSourceNode = {
//    case NT_White => createBufferSourceLooping(bufferNoiseWhite)
//    case NT_Pink => createBufferSourceLooping(bufferNoisePink)
//    case NT_Red => createBufferSourceLooping(bufferNoiseRed)
//    case NT_Brown => createBufferSourceLooping(bufferNoiseBrown)
//  }
//
//  private def createBufferNoise(valSeq: ValueSequence): AudioBuffer = {
//    val bufferSize = ctx.sampleRate.toInt * 2
//    val buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate.toInt)
//    val channel = buffer.getChannelData(0)
//    for (i <- 0 until bufferSize) {
//      channel.set(i, valSeq.nextValue.toFloat)
//    }
//    buffer
//  }
//
//  private def createBufferSourceLooping(buffer: AudioBuffer): AudioBufferSourceNode = {
//    val bufferSrc = ctx.createBufferSource()
//    bufferSrc.buffer = buffer
//    bufferSrc.loop = true
//    bufferSrc
//  }
//
//
//}



