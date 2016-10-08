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

trait AudioOutputAware {

  def audioOutput: AudioOutput

}

abstract class ControlParamJvmMinimAbstract extends ControlParam with UGenAware {

  def name: String

  override def toString: String = "ControlParamJvmMinimAbstract: " + name
}

/**
  * Jvm implementation of the DoctusSoundAudioContext
  */
case class DoctusSoundAudioContextJvmMinim() extends DoctusSoundAudioContext {


  private val _minim = {
    val fileLoader = FileLoaderUserHome()
    val serviceProvider = new JSMinim(fileLoader)
    new Minim(serviceProvider)
  }

  private val sys = ActorSystem.create()

  val _musicActor = sys.actorOf(MusicActor.props)

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

    def uGen: UGen = minimGain
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case s: UGenAware => minimGain.patch(s.uGen)
      case s: AudioOutputAware => minimGain.patch(s.audioOutput)
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
      case p: UGenAware => minimConstant.patch(p.uGen)
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

    def uGen: UGen = minimOscil
  }

  def start(time: Double): Unit = {
    // In minim patching an oscillator means to start it
    patchable.foreach(ugen => minimOscil.patch(ugen))
  }

  def stop(time: Double): Unit = ???

  def connect(sink: NodeSink): Unit = ???

  def connect(through: NodeThrough): NodeSource = {
    through match {
      case t: UGenAware =>
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

  def start(time: Double): Unit = ???

  def stop(time: Double): Unit = ???

  def connect(param: ControlParam): Unit = {
    param match {
      case p: UGenAware =>
        minimAdsr.patch(p.uGen)
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

object MusicActor {

  def props: Props = Props[MusicActor]

}

class MusicActor extends Actor {

  val eventHolder = TimeBasedEventHolder.empty[() => Unit]

  def receive: Receive = {
    case musicEvent: MusicEvent =>
      eventHolder.addEvent(musicEvent)
    case message: Any =>
      println(s"Received msg $message => unhandled")
      unhandled(message)
  }
}

trait TimeBasedEvent[T] {

  def executionTime: Double
  def data: T

}

case class TimeBasedEventHolderResult[T](nextHolder: TimeBasedEventHolder[T], events: List[TimeBasedEvent[T]])

object TimeBasedEventHolder {

  def empty[T] = TimeBasedEventHolderImpl[T](List.empty[TimeBasedEvent[T]])

  case class TimeBasedEventHolderImpl[T](initialEvents: List[TimeBasedEvent[T]]) extends TimeBasedEventHolder[T] {

    var events = initialEvents

    def detectEvents(time: Double): TimeBasedEventHolderResult[T] = {
      val resultEvents = events.filter{e => e.executionTime <= time}.sortBy(e => e.executionTime)
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
    * @param time defines which events have to be executed
    * @return The events and a new instance of the holder
    */
  def detectEvents(time: Double): TimeBasedEventHolderResult[T]

  /**
    * @param event that will be added to the holder
    */
  def addEvent(event: TimeBasedEvent[T]): Unit

}



