package doctus.sound

import net.entelijan.{NoiseBrown, NoisePink, NoiseWhite, ValueSequence}
import org.scalajs.dom._

trait ConnectableParam {

  def onConnect: NodeControl => Unit

}

trait WebAudioParamHolder {

  def addAudioParam(waParam: AudioParam)

}

trait AudioNodeAware {

  def audioNode: AudioNode

}

case class NodeSinkLineOutScalajs(waCtx: AudioContext) extends NodeSink with AudioNodeAware {

  val waDestination = waCtx.destination

  def audioNode: AudioNode = waDestination

}

case class NodeThroughGainScalajs(waCtx: AudioContext) extends NodeThroughGain with AudioNodeAware {

  val waGain = waCtx.createGain()

  val paramGain = new ConnectableParam with ControlParam {

    def onConnect = {
      case holder: WebAudioParamHolder => holder.addAudioParam(waGain.gain)
      case nodeControl =>
        println("control node %s is not a WebAudioParamHolder" format nodeControl)
        throw new IllegalStateException()
    }

    override def toString: String = "NodeThroughGainScalajs paramGain"

  }

  def connect(through: NodeThrough): NodeSource = {
    through match {
      case node: AudioNodeAware =>
        val src = node.audioNode
        waGain.connect(src)
      case _ =>
        println("through %s is not AudioNodeAware" format through)
        throw new IllegalStateException()
    }
    this
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case node: AudioNodeAware =>
        val src = node.audioNode
        waGain.connect(src)
      case _ =>
        println("sink %s is not AudioNodeAware" format sink)
        throw new IllegalStateException()
    }
  }

  def gain: ControlParam = paramGain

  def audioNode: AudioNode = waGain
}

case class NodeSourceOscilScalajs(waCtx: AudioContext, waveType: WaveType)
  extends NodeSourceOscil with AudioNodeAware with OscilUtil{

  def waveTypeStr = waWaveType(waveType)

  val waOscil = waCtx.createOscillator()
  waOscil.`type` = waveTypeStr

  val paramFrequency = new ControlParam with ConnectableParam {
    def onConnect: (NodeControl) => Unit = {
      case holder: WebAudioParamHolder =>
        holder.addAudioParam(waOscil.frequency)
      case nodeControl =>
        println("control node %s is not a WebAudioParamHolder" format nodeControl)
        throw new IllegalStateException()
    }

    override def toString: String = "NodeSourceOscilSineScalajs paramFrequency"
  }

  def connect(through: NodeThrough): NodeSource = {
    through match {
      case node: AudioNodeAware =>
        val waThrough = node.audioNode
        waOscil.connect(waThrough)
        through
      case _ =>
        println("through %s is not AudioNodeAware" format through)
        throw new IllegalStateException()
    }
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case node: AudioNodeAware =>
        val waSink = node.audioNode
        waOscil.connect(waSink)
      case _ =>
        println("sink %s is not AudioNodeAware" format sink)
        throw new IllegalStateException()
    }
  }

  def start(time: Double): Unit = {
    waOscil.start(time)
  }

  def stop(time: Double): Unit = {
    waOscil.stop(time)
  }

  def frequency: ControlParam = paramFrequency

  def audioNode: AudioNode = waOscil

}

case class NodeSourceNoiseWhiteScalajs(waCtx: AudioContext, noiseType: NoiseType) extends NodeSourceNoise {

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

  def connect(through: NodeThrough): NodeSource = {
    through match {
      case node: AudioNodeAware =>
        val waThrough = node.audioNode
        waSrc.connect(waThrough)
        through
      case _ =>
        println("through %s is not AudioNodeAware" format through)
        throw new IllegalStateException()
    }
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case node: AudioNodeAware =>
        val waSink = node.audioNode
        waSrc.connect(waSink)
      case _ =>
        println("sink %s is not AudioNodeAware" format sink)
        throw new IllegalStateException()
    }
  }

  def start(time: Double): Unit = {
    waSrc.start(time)
  }

  def stop(time: Double): Unit = {
    waSrc.stop(time)
  }

}

case class NodeControlConstantScalajs(value: Double)(waCtx: AudioContext)
  extends NodeControl with WebAudioParamHolder {

  def connect(param: ControlParam): Unit = {
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ =>
        println("control pram %s is not connectable" format param)
        throw new IllegalStateException()
    }
  }

  override def addAudioParam(waParam: AudioParam): Unit = {
    waParam.value = value
  }

}

case class NodeControlAdsrScalajs(attack: Double, decay: Double, sustain: Double, release: Double)(waCtx: AudioContext)
  extends NodeControlEnvelope with WebAudioParamHolder {

  val valMax = 1.0

  var waParamList = List.empty[AudioParam]

  def connect(param: ControlParam): Unit = {
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ =>
        println("control pram %s is not connectable" format param)
        throw new IllegalStateException()
    }
  }

  def start(time: Double): Unit = {
    waParamList.foreach { p =>
      p.cancelScheduledValues(time)
      p.setValueAtTime(0.0, time)
      p.linearRampToValueAtTime(valMax, time + attack)
      p.linearRampToValueAtTime(sustain * valMax, time + attack + decay)
    }
  }

  def stop(time: Double): Unit = {
    val now = waCtx.currentTime
    waParamList.foreach { p =>
      val  diff = time - now
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

trait OscilUtil {

  def waWaveType(waveType: WaveType) = waveType match {
    case WaveType_Sine => "sine"
    case WaveType_Triangle => "triangle"
    case WaveType_Sawtooth => "sawtooth"
  }

}

case class NodeControlLfoScalajs(waveType: WaveType, frequency: Double, amplitude: Double)(waCtx: AudioContext)
  extends NodeControlLfo with WebAudioParamHolder with OscilUtil {

  val waOscil = waCtx.createOscillator()
  waOscil.`type` = waWaveType(waveType)
  waOscil.frequency.value = frequency

  val waGain = waCtx.createGain()
  waGain.gain.value = amplitude

  waOscil.connect(waGain)


  def start(time: Double): Unit = {
    waOscil.start(time)
  }

  def stop(time: Double): Unit = {
    waOscil.stop(time)
  }

  def connect(param: ControlParam): Unit = {
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ =>
        println("control pram %s is not connectable" format param)
        throw new IllegalStateException()
    }
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

  def createNodeControlConstant(value: Double): NodeControl = {
    NodeControlConstantScalajs(value)(waCtx)
  }

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double): NodeControlEnvelope = {
    NodeControlAdsrScalajs(attack, decay, sustain, release)(waCtx)
  }

  def createNodeControlLfo(waveType: WaveType, frequency: Double, amplitude: Double): NodeControlLfo = {
    NodeControlLfoScalajs(waveType, frequency, amplitude)(waCtx)
  }
  
  def createNodeThroughFilter(filterType: FilterType): NodeThroughFilter = ???

  def currentTime: Double = waCtx.currentTime

}

