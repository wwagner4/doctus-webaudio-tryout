package doctus.sound

import java.io.{File, FileInputStream, InputStream}

import akka.actor.{Actor, ActorSystem, Props}
import ddf.minim.javasound.JSMinim
import ddf.minim.ugens._
import ddf.minim.{AudioOutput, Minim, UGen}

trait MinimContext {

  def minim: Minim

  def tell(message: Any): Unit

  def currentTime: Double

  def terminate: Unit

  def actorSystem: ActorSystem

}

trait UGenAware {

  def uGen: UGen

}

trait UGenInputAware {

  def uGenInput: UGen#UGenInput

}

trait AudioOutputAware {

  def audioOutput: AudioOutput

}

abstract class ControlParamJvmMinimAbstract extends ControlParam with UGenInputAware {

  def name: String

  override def toString: String = "ControlParamJvmMinimAbstract: " + name
}

/**
  * Jvm implementation of the DoctusSoundAudioContext
  */
case class DoctusSoundAudioContextJvmMinim() extends DoctusSoundAudioContext {

  val ctx = MinimContextDefault()

  def createNodeSinkLineOut: NodeSink = {
    NodeSinkJvmMinim(ctx)
  }

  def createNodeSourceOscil(waveType: WaveType): NodeSourceOscil = {
    NodeSourceOscilJvmMinim(waveType)(ctx)
  }

  def createNodeSourceNoise(noiseType: NoiseType): NodeSourceNoise = ???

  def createNodeThroughGain: NodeThroughGain = {
    NodeThroughGainJvmMinim(ctx)
  }

  def createNodeThroughFilter(filterType: FilterType): NodeThroughFilter = ???

  def createNodeThroughPan: NodeThroughPan = ???

  def createNodeThroughDelay: NodeThroughDelay = ???

  def createNodeControlConstant(value: Double): NodeControl = {
    NodeControlConstantJvmMinim(value)(ctx)
  }

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend): NodeControlEnvelope = {
    NodeControlAdsrJvmMinim(attack, decay, sustain, release, gain, trend)(ctx)
  }

  def createNodeControlLfo(waveType: WaveType): NodeControlLfo = ???

  def currentTime: Double = ctx.currentTime

  def sampleRate: Double = ???

  def terminate: Unit = {
    println(f"terminating at $currentTime%.2f")
    ctx.terminate
  }
}

case class NodeSinkJvmMinim(ctx: MinimContext) extends NodeSink with AudioOutputAware {

  val minimSink = ctx.minim.getLineOut

  def audioOutput: AudioOutput = minimSink
}

case class NodeThroughGainJvmMinim(ctx: MinimContext) extends NodeThroughGain with UGenAware {

  private val minimGain = new Gain()

  def gain: ControlParam = new ControlParamJvmMinimAbstract {

    def name: String = "NodeThroughGainJvmMinim::gain"

    def uGenInput = minimGain.gain
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case s: UGenAware =>
        println(s"connecting GAIN $minimGain to UGen ${s.uGen} ($sink)")
        minimGain.patch(s.uGen)
      case s: AudioOutputAware =>
        println(s"connecting GAIN $minimGain to AudioOutput ${s.audioOutput} ($sink)")
        minimGain.patch(s.audioOutput)
      case _ => throw new IllegalArgumentException(
        s"cannot connect $this to $sink. $sink is not 'UGenAware'")
    }
  }

  def connect(through: NodeThrough): NodeSource = {
    through match {
      case t: UGenAware =>
        println(s"Preparing to connect OSCIL $minimGain to UGen ${t.uGen} ($through) <- stored in option")
        minimGain.patch(t.uGen)
        through

      case _ =>
        throw new IllegalArgumentException(
          s"cannot connect $this to $through. $through is not 'UGenAware'")
    }
  }

  def uGen: UGen = minimGain
}

case class NodeControlConstantJvmMinim(value: Double)(ctx: MinimContext) extends NodeControl with UGenAware {

  val minimConstant = new Constant(value.toFloat)

  def connect(param: ControlParam): Unit = {
    param match {
      case p: UGenInputAware =>
        println(s"connecting CONSTANT $minimConstant to UGenInput ${p.uGenInput} ($param)")
        minimConstant.patch(p.uGenInput)
      case _ => throw new IllegalStateException(
        "Cannot connect %s to parameter %s. %s is not 'UGenAware'" format(this, param, param))
    }
  }

  def uGen: UGen = minimConstant
}

case class NodeSourceOscilJvmMinim(waveType: WaveType)(ctx: MinimContext) extends NodeSourceOscil {

  private val freq = 440f
  private val ampl = 1.0f
  private val minimOscil = new Oscil(freq, ampl, mapWaveType(waveType))

  private var patchable = Option.empty[UGen]

  private def mapWaveType(waveType: WaveType) = waveType match {
    case WaveType_Sine => Waves.SINE
    case WaveType_Sawtooth => Waves.SAW
    case WaveType_Square => Waves.SQUARE
    case WaveType_Triangle => Waves.TRIANGLE
  }

  def frequency: ControlParam = new ControlParamJvmMinimAbstract {

    def name: String = "NodeSourceOscilJvmMinim::frequency"

    def uGenInput = minimOscil.frequency
  }

  def start(time: Double): Unit = {
    val f = () => {
      // In minim patching an oscillator means to start it
      println(f"Starting (connecting) OSCIL $minimOscil to uGen $patchable at $time%.2f")
      patchable.foreach(ugen => minimOscil.patch(ugen))
    }
    val me = MusicEvent(time, f)
    println(s"Telling MUSICEVENT $me oscil.patch(ugen)")
    ctx.tell(me)
  }

  def stop(time: Double): Unit = {
    val f = () => {
      // In minim unpatching an oscillator means to stop it
      println(f"Stopping (unconnecting) OSCIL $minimOscil from uGen $patchable at $time%.2f")
      patchable.foreach(ugen => minimOscil.unpatch(ugen))
      // Prevent restart of the oscillator
      patchable = Option.empty[UGen]
    }
    val me = MusicEvent(time, f)
    println(s"Telling MUSICEVENT $me oscil.unpatch(ugen)")
    ctx.tell(me)
  }

  def connect(sink: NodeSink): Unit = ???

  def connect(through: NodeThrough): NodeSource = {
    through match {
      case t: UGenAware =>
        println(s"Preparing to connect OSCIL $minimOscil to UGen ${t.uGen} ($through) <- stored in option")
        patchable = Some(t.uGen)
        through
      case _ =>
        throw new IllegalArgumentException(
          s"cannot connect $this to $through. $through is not 'UGenAware'")
    }
  }
}

object TransitionFunctionFactory {

  def log(from: Double, to: Double, time: Double): Double => Double = {

    val rest = math.abs(from - to) / 2000.0
    val a = math.pow(math.E, -math.log(rest) / time)
    val d = from - to

    x => {
      if (x < time) d * math.pow(a, -x) - x * rest / time + to
      else to
    }

  }

  def lin(from: Double, to: Double, time: Double): Double => Double = {

    x => {
      if (x < time) from + (to - from) / time * x
      else to
    }

  }
}


