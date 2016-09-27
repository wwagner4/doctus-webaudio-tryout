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

  val frequencies = Stream.iterate(400.0)(f => f * math.pow(2, 1.0 / 3))

  def frequencyFrom(nineth: Nineth) = nineth match {
    case N_00 => frequencies(0)
    case N_01 => frequencies(1)
    case N_02 => frequencies(2)

    case N_10 => frequencies(3)
    case N_11 => frequencies(4)
    case N_12 => frequencies(5)

    case N_20 => frequencies(6)
    case N_21 => frequencies(7)
    case N_22 => frequencies(8)
  }

  def start(nineth: Nineth): Unit = {
    val frequency = frequencyFrom(nineth)
    val inst = Instrument(frequency)

    val now = ctx.currentTime
    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach(inst => inst.stop(now))
  }

  case class Instrument(frequency: Double) extends StartStoppable {

    val burst = createBurst
    val sink = ctx.createNodeSinkLineOut
    val masterGain = createGain(1.0)

    val delay = createDelay(0.001)
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

  def createBurst: NodeSource with StartStoppable = {

    val burst = ctx.createNodeSourceNoise(NoiseType_Pink)
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
