package doctus.sound

import java.io.{File, FileInputStream, InputStream}

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import ddf.minim.javasound.JSMinim
import ddf.minim.ugens._
import ddf.minim.{AudioOutput, Minim, UGen}


/**
  * Jvm implementation of the DoctusSoundAudioContext
  */
case class DoctusSoundAudioContextJvmMinim() extends DoctusSoundAudioContext {


  private val minim = {
    val fileLoader = FileLoaderUserHome()
    val serviceProvider = new JSMinim(fileLoader)
    new Minim(serviceProvider)
  }

  private val sys = ActorSystem.create()

  val musicActor = sys.actorOf(MusicActor.props)



  private val startTime = System.nanoTime()

  def createNodeSinkLineOut: NodeSink = {
    NodeSinkJvmMinim(minim)
  }

  def createNodeSourceOscil(waveType: WaveType): NodeSourceOscil = {
    NodeSourceOscilJvmMinim(waveType)(minim)
  }

  def createNodeSourceNoise(noiseType: NoiseType): NodeSourceNoise = ???

  def createNodeThroughGain: NodeThroughGain = {
    NodeThroughGainJvmMinim(minim)
  }

  def createNodeThroughFilter(filterType: FilterType): NodeThroughFilter = ???

  def createNodeThroughPan: NodeThroughPan = ???

  def createNodeThroughDelay: NodeThroughDelay = ???

  def createNodeControlConstant(value: Double): NodeControl = {
    NodeControlConstantJvmMinim(value)(minim)
  }

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend): NodeControlEnvelope = {
    NodeControlAdsrJvmMinim(attack, decay, sustain, release, gain, trend)(minim)
  }

  def createNodeControlLfo(waveType: WaveType): NodeControlLfo = ???

  def currentTime: Double = (System.nanoTime() - startTime) / 1000000000.0

  def sampleRate: Double = ???

  def terminate: Unit = {
    sys.terminate()
  }
}

case class NodeSinkJvmMinim(minim: Minim) extends NodeSink with AudioOutputAware {

  val minimSink = minim.getLineOut

  def audioOutput: AudioOutput = minimSink
}

case class NodeThroughGainJvmMinim(minim: Minim) extends NodeThroughGain with UGenAware {

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

case class NodeControlConstantJvmMinim(value: Double)(minim: Minim) extends NodeControl with UGenAware {

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

case class NodeSourceOscilJvmMinim(waveType: WaveType)(minim: Minim) extends NodeSourceOscil {

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
                                  (minim: Minim) extends NodeControlEnvelope {

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

object MusicActor {

  def props: Props = Props[MusicActor]

}

class MusicActor extends Actor {

  def receive: Receive = {
    case message: Any =>
      println(s"Received msg $message => unhandled")
      unhandled(message)
  }
}



