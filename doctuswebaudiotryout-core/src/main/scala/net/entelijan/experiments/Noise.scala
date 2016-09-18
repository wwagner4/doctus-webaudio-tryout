package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

/**
  * Creating some noise.
  */
case class Noise(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "noise"

  private val noiseTypes = List(NoiseType_White, NoiseType_Pink, NoiseType_Brown)
  private val lfoFreqs = List(3.0, 0.5, 0.2)

  var noiseOpt = Option.empty[StartStoppable]

  def start(nineth: Nineth): Unit = {

    val (noiseType, lfoFreq) = SoundUtil.xyParams(noiseTypes, lfoFreqs)(nineth)

    val lfoCtrl = ctx.createNodeControlLfo(WaveType_Sine, lfoFreq, 0.05)
    val constCtrl = ctx.createNodeControlConstant(0.1)

    val noise = ctx.createNodeSourceNoise(noiseType)
    val gain = ctx.createNodeThroughGain
    val out = ctx.createNodeSinkLineOut

    lfoCtrl >- gain.gain
    constCtrl >- gain.gain

    noise >- gain >- out

    lfoCtrl.start(0.0)
    noise.start(0.0)

    noiseOpt = Some(noise)
  }

  def stop(): Unit = {

    val t = ctx.currentTime
    noiseOpt.foreach(_.stop(t))
  }

}
