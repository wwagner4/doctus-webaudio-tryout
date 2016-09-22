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

  case class SrcParam(gainVal: Double, freq: Double, delay: Double)

  case class Inst() extends StartStoppable {

    def createSource(param: SrcParam): NodeSource with StartStoppable = {

      val gain = ctx.createNodeThroughGain
      val gainCtrl = ctx.createNodeControlConstant(param.gainVal)

      val oscil = ctx.createNodeSourceOscil(WaveType_Sine)
      val oscilCtrl = ctx.createNodeControlConstant(param.freq)

      val delay = ctx.createNodeThroughDelay
      val delayCtrl = ctx.createNodeControlConstant(param.delay)

      val envel = ctx.createNodeThroughGain
      val envelCtrl = ctx.createNodeControlAdsr(0.3, 0.0, 1.0, 2.5)

      delayCtrl >- delay.delay
      gainCtrl >- gain.gain
      oscilCtrl >- oscil.frequency
      envelCtrl >- envel.gain

      oscil >- gain >- envel >- delay


      new NodeSource with StartStoppable {

        def connect(sink: NodeSink): Unit = delay.connect(sink)

        def connect(through: NodeThrough): NodeSource = delay.connect(through)

        def start(time: Double): Unit = {
          oscil.start(0.0)
          envelCtrl.start(time)
        }

        def stop(time: Double): Unit = {
          envelCtrl.stop(time)
          oscil.stop(time + 3)
        }

      }

    }

    val gainSeq = Stream.iterate(0.07)(x => x * 0.999)
    val freqSeq = Stream.iterate(450.0)(x => x * 1.12599)
    val delaySeq = Stream.iterate(0.0)(x => x + 0.05)

    val sources = gainSeq.zip(freqSeq).zip(delaySeq)
      .map { case ((g, f), d) => SrcParam(g, f, d) }
      .map { p => createSource(p) }
      .take(4)

    val sink = ctx.createNodeSinkLineOut

    sources.foreach { src =>
      src >- sink
    }

    def start(time: Double): Unit = {
      sources.foreach { src =>
        src.start(time)
      }
    }

    def stop(time: Double): Unit = {
      sources.foreach { src =>
        src.stop(time)
      }
    }

  }
}