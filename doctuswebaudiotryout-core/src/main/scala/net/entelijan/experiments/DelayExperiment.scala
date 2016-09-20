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

  case class Inst() extends StartStoppable {

    def createSource(gainVal: Double): N = {

      val gain = ctx.createNodeThroughGain
      val gainCtrl = ctx.createNodeControlConstant(gainVal)

      val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
      
      gainCtrl >- gain.gain
      
      oscil >- gain

      new NodeSource with StartStoppable {

        def connect(sink: NodeSink): Unit = gain.connect(sink)

        def connect(through: NodeThrough): NodeSource = gain.connect(through)

        def start(time: Double): Unit = oscil.start(time)

        def stop(time: Double): Unit = oscil.stop(time)

      }

    }

    val sources = List(0.01, 0.01, 0.01).map { gain => createSource(gain) }
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