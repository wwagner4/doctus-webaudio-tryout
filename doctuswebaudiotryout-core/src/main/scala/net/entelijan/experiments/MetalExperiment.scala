package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

import scala.util.Random

/**
  * Example creating some metal like sound
  */
case class MetalExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title = "metal"

  val ran = Random

  val baseFreqList = List(222.0, 333.0, 444.0)
  val logarthmicDecayList = List(0.8, 0.6, 0.4)

  var gainableOscilsOpt = Option.empty[Seq[StartStoppableSource]]
  var adsrOpt = Option.empty[NodeControlEnvelope]

  def start(nineth: Nineth): Unit = {
    
    val (freq, ld) = SoundUtil.xyParams(baseFreqList, logarthmicDecayList)(nineth) 

    val harmonics = Stream.iterate(freq)(f => f * math.sqrt(2.0))
    val baseGain = 1.0 - 1.0 * ld // Base gain must be smaller if decay is slower
    val gains = Stream.iterate(baseGain)(g => g * ld)

    val params = harmonics.zip(gains).take(6).toList

    val gainableOscils = params.map { case (f, g) => gainableOscil(f, g) }

    val adsr = ctx.createNodeControlAdsr(0.001, 1.5, 0.1, 1.0, 0.5)
    val gain = ctx.createNodeThroughGain
    val out = ctx.createNodeSinkLineOut
    val now = ctx.currentTime

    adsr >- gain.gain
    gainableOscils.foreach(_ >- gain)
    gain >- out

    gainableOscils.foreach(_.start(now))
    adsr.start(now)

    gainableOscilsOpt = Some(gainableOscils)
    adsrOpt = Some(adsr)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    gainableOscilsOpt.foreach(_.foreach(_.stop(now + 10)))
    adsrOpt.foreach(_.stop(now))
  }

  def gainableOscil(freqVal: Double, gainVal: Double): StartStoppableSource = {

    val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
    val gain = ctx.createNodeThroughGain

    val freqCtrl = ctx.createNodeControlConstant(freqVal)
    val gainCtrl = ctx.createNodeControlConstant(gainVal)

    freqCtrl >- oscil.frequency
    gainCtrl >- gain.gain
    oscil >- gain

    StartStoppableSource(oscil, gain)
  }

  case class StartStoppableSource(startStoppable: StartStoppable, nodeSource: NodeSource) extends NodeSource with StartStoppable {

    def start(time: Double): Unit = startStoppable.start(time)

    def stop(time: Double): Unit = startStoppable.stop(time)

    def connect(sink: NodeSink): Unit = nodeSource.connect(sink)

    def connect(filter: NodeThrough): NodeSource = nodeSource.connect(filter)

  }

}
