package doctus.sound

import java.io.{File, FileInputStream, InputStream}

import akka.actor.{Actor, ActorSystem, Props}
import ddf.minim.javasound.JSMinim
import ddf.minim.ugens._
import ddf.minim.{AudioOutput, Minim, UGen}

trait MinimContext {

  def minim: Minim

  def tell(message: Any): Unit

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

  import scala.concurrent.duration._

  private val _minim = {
    val fileLoader = FileLoaderUserHome()
    val serviceProvider = new JSMinim(fileLoader)
    new Minim(serviceProvider)
  }

  val sys = ActorSystem.create()

  val _musicActor = sys.actorOf(MusicActor.props)

  val funcCreateTimeEvent = () => {
    val timeEvent = TimeEvent(currentTime)
    _musicActor ! timeEvent
  }

  sys.scheduler.schedule(0.second, 8000.microseconds)(funcCreateTimeEvent())(sys.dispatcher)

  val ctx = new MinimContext {

    def tell(message: Any): Unit = _musicActor ! message

    def minim: Minim = _minim

  }

  private val startTime = System.nanoTime()

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

  def currentTime: Double = (System.nanoTime() - startTime) / 1000000000.0

  def sampleRate: Double = ???

  def terminate: Unit = {
    println(f"terminating at $currentTime%.2f")
    sys.terminate()
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
        println(s"connecting GAIN $minimGain to UGen ${s.uGen}")
        minimGain.patch(s.uGen)
      case s: AudioOutputAware =>
        println(s"connecting GAIN $minimGain to AudioOutput ${s.audioOutput}")
        minimGain.patch(s.audioOutput)
      case _ => throw new IllegalArgumentException(
        s"cannot connect $this to $sink. $sink is not 'UGenAware'")
    }
  }

  def connect(through: NodeThrough): NodeSource = ???

  def uGen: UGen = minimGain
}

case class NodeControlConstantJvmMinim(value: Double)(ctx: MinimContext) extends NodeControl with UGenAware {

  val minimConstant = new Constant(value.toFloat)

  def connect(param: ControlParam): Unit = {
    param match {
      case p: UGenInputAware =>
        println(s"connecting CONSTANT $minimConstant to UGenInput ${p.uGenInput}")
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
        println(s"Preparing to connect OSCIL $minimOscil to UGen ${t.uGen} <- stored in option")
        patchable = Some(t.uGen)
        through
      case _ =>
        throw new IllegalArgumentException(
          s"cannot connect $this to $through. $through is not 'UGenAware'")
    }
  }
}

case class NodeControlAdsrJvmMinim(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)
                                  (ctx: MinimContext) extends NodeControlEnvelope {

  private val minimAdsr = new ADSR(gain.toFloat, attack.toFloat, decay.toFloat, sustain.toFloat, release.toFloat)

  def start(time: Double): Unit = {
    val func = () => {
      println(f"ADSR noteOn at $time%.2f")
      minimAdsr.noteOn()
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr.noteOn()")
    ctx.tell(me)
  }

  def stop(time: Double): Unit = {
    val func = () => {
      println(f"ADSR noteOff at $time%.2f")
      minimAdsr.noteOff()
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr.noteOff()")
    ctx.tell(me)
  }

  def connect(param: ControlParam): Unit = {
    param match {
      case p: UGenInputAware =>
        println(s"connecting ADSR $minimAdsr to ${p.uGenInput}")
        minimAdsr.patch(p.uGenInput)
        ()
      case _ =>
        throw new IllegalArgumentException(
          s"cannot connect $this to $param. $param is not 'UGenAware'")
    }
  }
}

case class FileLoaderUserHome() {

  def sketchPath(fileName: String): String = {
    val file = getCreateFile(fileName)
    file.getAbsolutePath
  }

  def createInput(fileName: String): InputStream = {
    new FileInputStream(fileName)
  }

  private def getCreateFile(fileName: String): File = {
    val home = new File(System.getProperty("user.home"))
    val outDir = new File(home, "minim_out")
    outDir.mkdirs()
    new File(outDir, fileName)
  }

}

// Not serializable. Though the best solution (eventually)
// as long as we stay within one VM
case class MusicEvent(executionTime: Double, data: () => Unit) extends TimeBasedEvent[() => Unit]

case class TimeEvent(time: Double)

object MusicActor {

  def props: Props = Props[MusicActor]

}

class MusicActor extends Actor {

  var eventHolder = TimeBasedEventHolder.empty[() => Unit]

  def receive: Receive = {
    case musicEvent: MusicEvent =>
      eventHolder.addEvent(musicEvent)
    case TimeEvent(time) =>
      val r = eventHolder.detectEvents(time)
      r.events.foreach(evt => evt.data())
      eventHolder = r.nextHolder
    case message: Any =>
      unhandled(message)
  }
}

trait TimeBasedEvent[T] {

  def executionTime: Double

  def data: T

}

case class TimeBasedEventHolderResult[T](nextHolder: TimeBasedEventHolder[T], events: List[TimeBasedEvent[T]])

object TimeBasedEventHolder {

  def empty[T]: TimeBasedEventHolder[T] = TimeBasedEventHolderImpl[T](List.empty[TimeBasedEvent[T]])

  case class TimeBasedEventHolderImpl[T](initialEvents: List[TimeBasedEvent[T]]) extends TimeBasedEventHolder[T] {

    var events = initialEvents

    def detectEvents(time: Double): TimeBasedEventHolderResult[T] = {
      val resultEvents = events.filter { e => e.executionTime <= time }.sortBy(e => e.executionTime)
      val restEvents = events.diff(resultEvents)
      TimeBasedEventHolderResult(TimeBasedEventHolderImpl(restEvents), resultEvents)
    }

    def addEvent(event: TimeBasedEvent[T]): Unit = {
      events = event :: events
    }

  }

}

trait TimeBasedEventHolder[T] {

  /**
    * Detects all events with 'executionTime' before or equal to 'time'
    * These events will be returned and removed from the holder
    *
    * @param time defines which events have to be executed
    * @return The events and a new instance of the holder
    */
  def detectEvents(time: Double): TimeBasedEventHolderResult[T]

  /**
    * @param event that will be added to the holder
    */
  def addEvent(event: TimeBasedEvent[T]): Unit

}




