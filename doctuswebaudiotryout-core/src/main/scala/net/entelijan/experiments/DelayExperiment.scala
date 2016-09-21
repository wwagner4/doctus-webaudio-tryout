package net.entelijan.experiments

import net.entelijan.SoundExperiment
import net.entelijan.Nineth
import doctus.sound.DoctusSoundAudioContext
import doctus.sound.StartStoppable
import doctus.sound.WaveType_Sine
import doctus.sound.NodeSource
import doctus.sound.NodeThrough
import doctus.sound.NodeSink

case class DelayExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "delay"

  var instOpt = Option.empty[Inst]

  def start(nineth: Nineth): Unit = {
    val now = ctx.currentTime
    val inst = Inst()
    inst.start(now)
    instOpt = Some(inst)
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach { _.stop(now) }
  }

  type N = NodeSource with StartStoppable

  case class SrcParam(gainVal: Double, freq: Double)

  case class Inst() extends StartStoppable {

    def createSource(param: SrcParam): N = {
      println("param:%s" format param)

      val gain = ctx.createNodeThroughGain
      val gainCtrl = ctx.createNodeControlConstant(param.gainVal)

      val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
      val oscilCtrl = ctx.createNodeControlConstant(param.freq)

      gainCtrl >- gain.gain

      oscil >- gain

      new NodeSource with StartStoppable {

        def connect(sink: NodeSink): Unit = gain.connect(sink)

        def connect(through: NodeThrough): NodeSource = gain.connect(through)

        def start(time: Double): Unit = oscil.start(time)

        def stop(time: Double): Unit = oscil.stop(time)

      }

    }

    val gainSeq = Stream.iterate(0.07)(x => x * 0.999)
    val freqSeq = Stream.iterate(350.0)(x => x * 1.2345)

    val sources = gainSeq.zip(freqSeq)
      .map { case (g, f) => SrcParam(g, f) }
      .map { p => createSource(p) }
      .take(4)

    val sink = ctx.createNodeSinkLineOut

    sources.foreach { src =>
      src >- sink
    }

    def start(time: Double): Unit = {
      sources.foreach { src =>
        src.start(time)
        println("INST started at %.2f" format time)
      }
    }

    def stop(time: Double): Unit = {
      sources.foreach { src =>
        src.stop(time)
        println("INST stopped at %.2f" format time)
      }
    }

  }
}