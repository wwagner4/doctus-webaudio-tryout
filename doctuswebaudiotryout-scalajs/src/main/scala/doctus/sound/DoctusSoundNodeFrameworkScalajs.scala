package doctus.sound

import net.entelijan.{NoiseBrown, NoisePink, NoiseWhite, ValueSequence}
import org.scalajs.dom._

trait ConnectableParam {

  def onConnect: NodeControl => Unit

}

/**
  * Accepts audio parameters and holds them for further use.
  * The way how audio parameters are stored is left to
  * the implementing class
  */
trait WebAudioParamHolder {

  def addAudioParam(waParam: AudioParam)

}

/**
  * Enables a node to connect to control Parameters
  */
trait ParamConnectable {

  def self: NodeControl

  def connect(param: ControlParam): Unit = {
    param match {
      case connectable: ConnectableParam => connectable.onConnect(self)
      case _ =>
        println("control pram %s is not connectable" format param)
        throw new IllegalStateException()
    }
  }

}

/**
  * Helper thread to create audio parameters for the scalajs implementation
  */
trait ControlParamFactory {

  def createParam(audioParam: () => AudioParam, description: String) = new ControlParam with ConnectableParam {
    def onConnect: (NodeControl) => Unit = {
      case holder: WebAudioParamHolder =>
        holder.addAudioParam(audioParam())
      case nodeControl =>
        println("control node %s is not a WebAudioParamHolder" format nodeControl)
        throw new IllegalStateException()
    }

    override def toString: String = description
  }

}

/**
  * Implementing classes contain an audio node they want to
  * expose to other classes
  */
trait AudioNodeAware {

  def audioNode: AudioNode

  def connect(through: NodeThrough): NodeSource = {
    through match {
      case node: AudioNodeAware =>
        val src = node.audioNode
        audioNode.connect(src)
      case cont: NodeThroughContainer =>
        connect(cont.sink)
        cont.source
      case _ =>
        println("through %s is not AudioNodeAware" format through)
        throw new IllegalStateException()
    }
    through
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case node: AudioNodeAware =>
        val src = node.audioNode
        audioNode.connect(src)
      case cont: NodeThroughContainer =>
        connect(cont.sink)
        ()
      case _ =>
        println("sink %s is not AudioNodeAware" format sink)
        throw new IllegalStateException()
    }
  }

}

case class NodeSinkLineOutScalajs(waCtx: AudioContext) extends NodeSink
  with AudioNodeAware {

  val waDestination = waCtx.destination

  def audioNode: AudioNode = waDestination

}

case class NodeThroughGainScalajs(waCtx: AudioContext) extends NodeThroughGain
  with AudioNodeAware with ControlParamFactory {

  private val waGain = waCtx.createGain()

  lazy val gain: ControlParam = createParam(() => waGain.gain, "NodeThroughGainScalajs gain")

  def audioNode: AudioNode = waGain

}

case class NodeThroughPanScalajs(waCtx: AudioContext)
  extends NodeThroughPan
    with AudioNodeAware with ControlParamFactory {

  private val waPan = waCtx.createStereoPanner()

  lazy val pan: ControlParam = createParam(() => waPan.pan, "NodeThroughPanScalajs pan")

  def audioNode: AudioNode = waPan

}

case class NodeThroughDelayScalajs(waCtx: AudioContext) extends NodeThroughDelay
  with AudioNodeAware with ControlParamFactory {

  private val waDelay = waCtx.createDelay(10)
  waDelay.delayTime.value = 0.1

  lazy val delay: ControlParam = createParam(() => waDelay.delayTime, "NodeThroughDelayScalajs delay")

  def audioNode: AudioNode = waDelay

}

case class NodeThroughFilterScalajs(filterType: FilterType)(waCtx: AudioContext) extends NodeThroughFilter
  with AudioNodeAware with ControlParamFactory {

  private val waFilter = waCtx.createBiquadFilter()

  private val filterTypeStr = filterType match {
    case FilterType_Lowpass => "lowpass"
    case FilterType_Highpass => "highpass"
    case FilterType_Bandpass => "bandpass"
    // TODO Extend for further filter types ???
  }
  waFilter.`type` = filterTypeStr

  // Set default values
  waFilter.Q.value = 5.0
  waFilter.frequency.value = 440
  waFilter.gain.value = 1.0
  waFilter.detune.value = 0.0

  lazy val frequency: ControlParam = createParam(() => waFilter.frequency, "NodeThroughFilterScalajs frequency")

  lazy val quality: ControlParam = createParam(() => waFilter.Q, "NodeThroughFilterScalajs quality")

  def audioNode: AudioNode = waFilter

}

case class NodeSourceOscilScalajs(waCtx: AudioContext, waveType: WaveType) extends NodeSourceOscil
  with AudioNodeAware with ControlParamFactory with OscilUtil {

  private val waOscil = waCtx.createOscillator()
  waOscil.`type` = waWaveType(waveType)

  def start(time: Double): Unit = {
    waOscil.start(time)
  }

  def stop(time: Double): Unit = {
    waOscil.stop(time)
  }

  lazy val frequency: ControlParam = createParam(() => waOscil.frequency, "NodeSourceOscilScalajs frequency")

  def audioNode: AudioNode = waOscil

}

case class NodeSourceNoiseWhiteScalajs(waCtx: AudioContext, noiseType: NoiseType) extends NodeSourceNoise
  with AudioNodeAware {

  val valueSeq: ValueSequence = noiseType match {
    case NoiseType_White => NoiseWhite
    case NoiseType_Pink => NoisePink()
    case NoiseType_Red => NoiseBrown(waCtx.sampleRate)
    case NoiseType_Brown => NoiseBrown(waCtx.sampleRate)
  }

  private lazy val bufferNoiseWhite = createBufferNoise(valueSeq)

  private def createBufferNoise(valSeq: ValueSequence): AudioBuffer = {
    val bufferSize = waCtx.sampleRate.toInt * 2
    val buffer = waCtx.createBuffer(1, bufferSize, waCtx.sampleRate.toInt)
    val channel = buffer.getChannelData(0)
    for (i <- 0 until bufferSize) {
      channel.set(i, valSeq.nextValue.toFloat)
    }
    buffer
  }

  private def createBufferSourceLooping(buffer: AudioBuffer): AudioBufferSourceNode = {
    val bufferSrc = waCtx.createBufferSource()
    bufferSrc.buffer = buffer
    bufferSrc.loop = true
    bufferSrc
  }

  val waSrc = createBufferSourceLooping(bufferNoiseWhite)

  def start(time: Double): Unit = {
    waSrc.start(time)
  }

  def stop(time: Double): Unit = {
    waSrc.stop(time)
  }

  def audioNode: AudioNode = waSrc
}

case class NodeControlConstantScalajs(value: Double)(waCtx: AudioContext)
  extends NodeControl with WebAudioParamHolder with ParamConnectable {

  def self = this

  override def addAudioParam(waParam: AudioParam): Unit = {
    waParam.value = value
  }

}

case class NodeControlAdsrScalajs(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double)
                                 (waCtx: AudioContext)
  extends NodeControlEnvelope
    with WebAudioParamHolder with ParamConnectable {

  def self = this

  var waParamList = List.empty[AudioParam]

  def start(time: Double): Unit = {
    waParamList.foreach { p =>
      p.cancelScheduledValues(time)
      p.setValueAtTime(0.0, time)
      p.linearRampToValueAtTime(gain, time + attack)
      p.linearRampToValueAtTime(sustain * gain, time + attack + decay)
    }
  }

  def stop(time: Double): Unit = {
    val now = waCtx.currentTime
    waParamList.foreach { p =>
      val diff = time - now
      if (diff <= 0.0) p.cancelScheduledValues(0.0)
      else p.cancelScheduledValues(time)
      p.linearRampToValueAtTime(0.0, time + release)
    }
  }

  def addAudioParam(waParam: AudioParam): Unit = {
    waParam.value = 0.0
    waParamList ::= waParam
  }
}

case class NodeControlLfoScalajs(waveType: WaveType)(waCtx: AudioContext) extends NodeControlLfo
  with WebAudioParamHolder with ControlParamFactory with OscilUtil with ParamConnectable {

  def self = this

  private val waOscil = waCtx.createOscillator()
  waOscil.`type` = waWaveType(waveType)
  waOscil.frequency.value = 400.0
  // default value

  private val waGain = waCtx.createGain()
  waGain.gain.value = 1.0 // Default value

  waOscil.connect(waGain)

  lazy val frequency: ControlParam = createParam(() => waOscil.frequency, "NodeControlLfoScalajs frequency")

  lazy val amplitude: ControlParam = createParam(() => waGain.gain, "NodeControlLfoScalajs amplitude")

  def start(time: Double): Unit = {
    waOscil.start(time)
  }

  def stop(time: Double): Unit = {
    waOscil.stop(time)
  }

  def addAudioParam(waParam: AudioParam): Unit = {
    waGain.connect(waParam)
  }

}

case class DoctusSoundAudioContextScalajs(waCtx: AudioContext) extends DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSink = {
    NodeSinkLineOutScalajs(waCtx)
  }

  def createNodeSourceOscil(waveType: WaveType): NodeSourceOscil = {
    NodeSourceOscilScalajs(waCtx, waveType)
  }

  def createNodeSourceNoise(noiseType: NoiseType): NodeSourceNoise = {
    NodeSourceNoiseWhiteScalajs(waCtx, noiseType)
  }

  def createNodeThroughGain: NodeThroughGain = {
    NodeThroughGainScalajs(waCtx)
  }

  def createNodeThroughPan: NodeThroughPan = {
    NodeThroughPanScalajs(waCtx)
  }

  def createNodeThroughDelay: NodeThroughDelay = {
    NodeThroughDelayScalajs(waCtx)
  }


  def createNodeControlConstant(value: Double): NodeControl = {
    NodeControlConstantScalajs(value)(waCtx)
  }

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend): NodeControlEnvelope = {
    NodeControlAdsrScalajs(attack, decay, sustain, release, gain)(waCtx)
  }

  def createNodeControlLfo(waveType: WaveType): NodeControlLfo = {
    NodeControlLfoScalajs(waveType)(waCtx)
  }

  def createNodeThroughFilter(filterType: FilterType): NodeThroughFilter = {
    NodeThroughFilterScalajs(filterType)(waCtx)
  }

  def currentTime: Double = waCtx.currentTime

  def sampleRate: Double = waCtx.sampleRate

}

trait OscilUtil {

  def waWaveType(waveType: WaveType) = waveType match {
    case WaveType_Sine => "sine"
    case WaveType_Triangle => "triangle"
    case WaveType_Sawtooth => "sawtooth"
    case WaveType_Square => "square"
  }

}



