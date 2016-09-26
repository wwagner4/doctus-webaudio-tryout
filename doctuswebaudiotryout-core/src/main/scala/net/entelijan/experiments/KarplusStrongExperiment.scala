package net.entelijan.experiments

import doctus.sound._
import net.entelijan._

/**
  * Synthesis for the simulation of string instruments.
  *
  * https://en.wikipedia.org/wiki/Karplus–Strong_string_synthesis
  */
case class KarplusStrongExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "karplus-strong"

  var instOpt = Option.empty[Instrument]

  def start(nineth: Nineth): Unit = {
    println("started")
    val inst = Instrument()

    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument() extends StartStoppable {

    val sink = ctx.createNodeSinkLineOut
    val ks = InstrumentKarplusStrong()
    val adsr = nodeAdsr

    ks >- adsr >- sink

    def start(time: Double): Unit = {
      adsr.start(time)
      ks.start(time)
    }

    def stop(time: Double): Unit = {
      adsr.stop(time)
      ks.stop(time)
    }

    // Reusable ???
    def nodeAdsr: NodeThrough with StartStoppable = {
      val gain = ctx.createNodeThroughGain
      val adsr = ctx.createNodeControlAdsr(0.01, 0.0, 1.0, 0.1)

      adsr >- gain.gain

      new NodeThroughContainer  with StartStoppable {

        def start(time: Double): Unit = {
          adsr.start(time)
        }

        def stop(time: Double): Unit = {
          println("adsr stop %.2f" format time)
          adsr.stop(time)
        }

        def source: NodeSource = gain

        def sink: NodeSink = gain
      }
    }

  }

  // Reusable ???
  case class InstrumentKarplusStrong() extends NodeSource with StartStoppable {

    val frequency = 440.0 // Herz

    val nodeNoise = ctx.createNodeSourceNoise(NoiseType_White)
    val nodeGain = ctx.createNodeThroughGain
    val nodeDelay = delayNode(0.01)
    val nodeFilter = lowpassNode(frequency)
    val nodeAttenuation = attenuationNode(0.6)

    nodeNoise >- nodeGain >- nodeDelay >- nodeFilter >- nodeAttenuation >- nodeGain

    def connect(sink: NodeSink): Unit = {
      nodeGain.connect(sink)
    }

    def connect(through: NodeThrough): NodeSource = {
      nodeGain.connect(through)
    }

    def start(time: Double): Unit = {
      nodeNoise.start(time)
      val duration = 0.01
      println("dur:" + duration)
      nodeNoise.stop(time + duration )
      println("KS started")
    }

    def stop(time: Double): Unit = {
      println("KS stopped")
      // Nothing to do
    }

  }

  def delayNode(delay: Double): NodeThrough = {

    val nodeConst = ctx.createNodeControlConstant(delay)
    val nodeDelay = ctx.createNodeThroughDelay

    nodeConst >- nodeDelay.delay

    nodeDelay
  }

  def lowpassNode(frequency: Double): NodeThrough = {

    val nodeConst = ctx.createNodeControlConstant(frequency)
    val nodeFilter = ctx.createNodeThroughFilter(FilterType_Lowpass)

    nodeConst >- nodeFilter.frequency

    nodeFilter
  }

  def attenuationNode(attenuation: Double): NodeThrough = {

    val nodeConst = ctx.createNodeControlConstant(attenuation)
    val nodeGain = ctx.createNodeThroughGain

    nodeConst >- nodeGain.gain

    nodeGain
  }
}
