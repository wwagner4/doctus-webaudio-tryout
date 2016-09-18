package net.entelijan.experiments

import doctus.sound._
import net.entelijan.{Nineth, SoundExperiment, SoundUtil}

import scala.util.Random

/**
  * Created by wwagner4 on 18/09/16.
  */
case class MetalTryout(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title = "metal"

  val ran = Random
  val baseFreqs = List(111, 222, 333, 444, 555)

  var gainableOscilsOpt = Option.empty[Seq[StartStoppableSource]]
  var adsrOpt = Option.empty[NodeControlEnvelope]

  case class StartStoppableSource(startStoppable: StartStoppable, nodeSource: NodeSource) extends NodeSource with StartStoppable {

    def start(time: Double): Unit = startStoppable.start(time)

    def stop(time: Double): Unit = startStoppable.stop(time)

    def connect(sink: NodeSink): Unit = nodeSource.connect(sink)

    def connect(filter: NodeThrough): NodeSource = nodeSource.connect(filter)

  }

  def start(nineth: Nineth): Unit = {

    def ranFreq: Double = baseFreqs(ran.nextInt(baseFreqs.size))
    val harmonics = SoundUtil.metalHarmonics(ranFreq, 6)
    val gains = Stream.from(0).map(SoundUtil.logarithmicDecay(1.4)(_)).map(_ * 0.5)

    val params = harmonics.zip(gains)

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

}
