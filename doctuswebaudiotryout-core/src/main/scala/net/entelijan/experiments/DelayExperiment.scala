package net.entelijan.experiments

import net.entelijan.{Nineth, SoundExperiment, SoundUtil}
import doctus.sound._

case class DelayExperiment(ctx: DoctusSoundAudioContext) extends SoundExperiment {

  def title: String = "delay"

  var instOpt = Option.empty[Inst]

  val attackValues = List(0.011, 0.1, 0.6)
  val delayValues = List(0.01, 0.1, 0.2)
  val frequencies = Stream.iterate(200.0)(x => x * 1.1).take(10).toList

  var freqIndex = 0

  def start(nineth: Nineth): Unit = {

    val (a, d) = SoundUtil.xyParams(attackValues, delayValues)(nineth)

    val f = frequencies(freqIndex)
    val now = ctx.currentTime
    val inst = Inst(a, d, f)
    inst.start(now)
    instOpt = Some(inst)
    freqIndex = (freqIndex + 1) % frequencies.size
  }

  def stop(): Unit = {
    val now = ctx.currentTime
    instOpt.foreach { _.stop(now) }
  }

  case class SrcParam(gainVal: Double, freq: Double, delay: Double)

  case class Inst(attack: Double, delayDiff: Double, baseFreq: Double) extends StartStoppable {

    val gainSeq = Stream.iterate(0.2)(x => x * 0.6)
    val freqSeq = Stream.iterate(baseFreq)(x => x * (4.0 / 3.0))
    val delaySeq = Stream.iterate(0.0)(x => x + delayDiff)
    val masterGain = createMasterGain

    val sources = gainSeq.zip(freqSeq).zip(delaySeq)
      .map { case ((g, f), d) => SrcParam(g, f, d) }
      .map { p => createSource(p, attack) }
      .take(10)

    val sink = ctx.createNodeSinkLineOut

    sources.foreach { src =>
      src >- masterGain
    }

    masterGain >- sink

    def start(time: Double): Unit = {
      sources.foreach { src =>
        src.start(time)
      }
      masterGain.start(time)
    }

    def stop(time: Double): Unit = {
      sources.foreach { src =>
        src.stop(time)
      }
      masterGain.stop(time)
    }

  }


  def createMasterGain: NodeThrough with StartStoppable = {

    val gain = ctx.createNodeThroughGain
    val adsr = ctx.createNodeControlAdsr(0.01, 0.0, 1.0, 2.0)

    adsr >-  gain.gain

    new NodeThroughContainer with StartStoppable {

      def source: NodeSource = gain

      def sink: NodeSink = gain

      def start(time: Double): Unit = adsr.start(time)

      def stop(time: Double): Unit = adsr.stop(time)

    }

  }

  def createSource(param: SrcParam, attack: Double): NodeSource with StartStoppable = {

    val gain = ctx.createNodeThroughGain
    val gainCtrl = ctx.createNodeControlConstant(param.gainVal)

    val oscil = ctx.createNodeSourceOscil(WaveType_Triangle)
    val oscilCtrl = ctx.createNodeControlConstant(param.freq)

    val delay = ctx.createNodeThroughDelay
    val delayCtrl = ctx.createNodeControlConstant(param.delay)

    val envel = ctx.createNodeThroughGain
    val envelCtrl = ctx.createNodeControlAdsr(attack, 0.0, 1.0, 2.5)

    delayCtrl >- delay.delay
    gainCtrl >- gain.gain
    oscilCtrl >- oscil.frequency
    envelCtrl >- envel.gain

    oscil >- gain >- envel >- delay


    new NodeSource with StartStoppable {

      def connect(sink: NodeSink): Unit = delay.connect(sink)

      def connect(through: NodeThrough): NodeSource = delay.connect(through)

      def start(time: Double): Unit = {
        oscil.start(time)
        envelCtrl.start(time)
      }

      def stop(time: Double): Unit = {
        envelCtrl.stop(time)
        oscil.stop(time + 10)
      }

    }

  }

}