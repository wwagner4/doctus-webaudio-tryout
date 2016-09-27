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

  val delayTimes = List(0.0005, 0.001, 0.005)
  val noiseTypes = List(NoiseType_White, NoiseType_Brown, NoiseType_Pink)

  def start(nineth: Nineth): Unit = {
    val (d, t) = SoundUtil.xyParams(delayTimes, noiseTypes)(nineth)

    val inst = Instrument(d, t)

    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument(delayTime: Double, noiseType: NoiseType) extends StartStoppable {

    val burst = createBurst(noiseType)
    val sink = ctx.createNodeSinkLineOut
    val masterGain = createGain(1.0)

    val delay = createDelay(delayTime)
    val filter = createFilter(5000, -3.0, FilterType_Lowpass)
    val attenuation = createGain(0.99)

    burst >- masterGain >- sink
    burst >- delay >- filter >- attenuation >- delay
    attenuation >- masterGain

    def start(time: Double): Unit = {
      burst.start(time)
      burst.stop(time + 0.01)
    }

    def stop(time: Double): Unit = {
      // Nothing to do
    }

  }

  def createBurst(noiseType: NoiseType): NodeSource with StartStoppable = {

    println("noise type " + noiseType)

    val burst = ctx.createNodeSourceNoise(noiseType)
    val burstGain = ctx.createNodeThroughGain

    val adsr = ctx.createNodeControlAdsr(0.0001, 0.0, 1.0, 0.0001)

    adsr >- burstGain.gain

    burst >- burstGain

    // TODO Create some NodeSourceContainer to avoid reimplementation of the connect methods
    new NodeSource with StartStoppable {

      def connect(sink: NodeSink): Unit = burstGain.connect(sink)

      def connect(through: NodeThrough): NodeSource = burstGain.connect(through)

      def start(time: Double): Unit = {
        burst.start(time)
        adsr.start(time)
      }

      def stop(time: Double): Unit = {
        burst.stop(time + 1)
        adsr.stop(time)
      }

    }

  }

  def createGain(gainVal: Double): NodeThrough = {
    val gain = ctx.createNodeThroughGain
    val gainCtrl = ctx.createNodeControlConstant(gainVal)

    gainCtrl >- gain.gain

    gain
  }

  def createFilter(frequency: Double, quality: Double, filterType: FilterType): NodeThroughFilter = {

    val filter = ctx.createNodeThroughFilter(filterType)
    val ctrlFreq = ctx.createNodeControlConstant(frequency)
    val ctrlQ = ctx.createNodeControlConstant(quality)

    ctrlFreq >- filter.frequency
    ctrlQ >- filter.quality

    filter
  }

  def createDelay(time: Double): NodeThrough = {
    val delay = ctx.createNodeThroughDelay
    val timeCtrl = ctx.createNodeControlConstant(time)

    timeCtrl >- delay.delay

    delay
  }
}
