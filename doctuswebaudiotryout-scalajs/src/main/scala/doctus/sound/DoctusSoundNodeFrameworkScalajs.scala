package doctus.sound

import org.scalajs.dom.{AudioContext, AudioNode, AudioParam}

trait ConnectableParam {

  def onConnect: NodeControl => Unit

}

trait WebAudioParamHolder {

  def addAudioParam(waParam: AudioParam)

}

trait AudioNodeAware {

  def audioNode: AudioNode

}

case class NodeSinkLineOutScalajs(waCtx: AudioContext) extends NodeSinkLineOut with AudioNodeAware {

  val waDestination = waCtx.destination

  def audioNode: AudioNode = waDestination

}

case class NodeFilterGainScalajs(waCtx: AudioContext) extends NodeFilterGain with AudioNodeAware {

  val waGain = waCtx.createGain()

  val paramGain = new ConnectableParam with ControlParam {

    def onConnect = nodeControl => {
      nodeControl match {
        case holder: WebAudioParamHolder => holder.addAudioParam(waGain.gain)
        case _ => throw new IllegalStateException("control node %s is not a WebAudioParamHolder" format nodeControl)
      }
    }

    override def toString: String = "NodeFilterGainScalajs paramGain"

  }

  def connect(filter: NodeFilter): NodeSource = {
    filter match {
      case node: AudioNodeAware => {
        val src = node.audioNode
        waGain.connect(src)
      }
      case _ => throw new IllegalStateException("filter %s is not AudioNodeAware" format filter)
    }
    this
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case node: AudioNodeAware => {
        val src = node.audioNode
        waGain.connect(src)
      }
      case _ => throw new IllegalStateException("sink %s is not AudioNodeAware" format sink)
    }
  }

  def gain: ControlParam = paramGain

  def audioNode: AudioNode = waGain
}

case class NodeSourceOscilSineScalajs(waCtx: AudioContext) extends NodeSourceOscilScalajs {

  def waveType = "sine"

}

case class NodeSourceOscilSawtoothScalajs(waCtx: AudioContext) extends NodeSourceOscilScalajs {

  def waveType = "sawtooth"

}

trait NodeSourceOscilScalajs extends NodeSourceOscilSine with AudioNodeAware {

  def waCtx: AudioContext

  def waveType: String

  val waOscil = waCtx.createOscillator()
  waOscil.`type` = waveType

  val paramFrequency = new ControlParam with ConnectableParam {
    def onConnect: (NodeControl) => Unit = nodeControl => {
      nodeControl match {
        case holder: WebAudioParamHolder => {
          holder.addAudioParam(waOscil.frequency)
        }
        case _ => throw new IllegalStateException("control node %s is not a WebAudioParamHolder" format nodeControl)
      }
    }

    override def toString: String = "NodeSourceOscilSineScalajs paramFrequency"
  }

  def connect(filter: NodeFilter): NodeSource = {
    filter match {
      case node: AudioNodeAware => {
        val waFilter = node.audioNode
        waOscil.connect(waFilter)
      }
      case _ => throw new IllegalStateException("filter %s is not AudioNodeAware" format filter)
    }
    filter
  }

  def connect(sink: NodeSink): Unit = {
    sink match {
      case node: AudioNodeAware => {
        val waSink = node.audioNode
        waOscil.connect(waSink)
      }
      case _ => throw new IllegalStateException("sink %s is not AudioNodeAware" format sink)
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

case class NodeControlConstantScalajs(value: Double)(waCtx: AudioContext)
  extends NodeControlConstant with WebAudioParamHolder {

  def connect(param: ControlParam): Unit = {
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ => throw new IllegalStateException("control pram %s is not connectable" format param)
    }
  }

  override def addAudioParam(waParam: AudioParam): Unit = {
    waParam.value = value
  }

}

case class NodeControlAdsrScalajs(attack: Double, decay: Double, sustain: Double, release: Double)(waCtx: AudioContext)
  extends NodeControlAdsr with WebAudioParamHolder {

  val valMax = 1.0

  var waParamList = List.empty[AudioParam]

  def connect(param: ControlParam): Unit = {
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ => throw new IllegalStateException("control pram %s is not connectable" format param)
    }
  }

  def stop(time: Double): Unit = {
    waParamList.foreach { p =>
      p.cancelScheduledValues(0)
      p.setValueAtTime(p.value, time)
      p.linearRampToValueAtTime(0.0, time + release)
    }
  }

  def start(time: Double): Unit = {
    waParamList.foreach { p =>
      val currentValue = p.value
      p.cancelScheduledValues(0)
      p.setValueAtTime(currentValue, time)
      p.linearRampToValueAtTime(valMax, time + attack)
      p.linearRampToValueAtTime(sustain * valMax, time + attack + decay)
    }
  }

  def addAudioParam(waParam: AudioParam): Unit = {
    waParam.value = 0.0
    waParamList ::= waParam
  }
}

case class DoctusSoundAudioContextScalajs(waCtx: AudioContext) extends DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSinkLineOut = {
    NodeSinkLineOutScalajs(waCtx)
  }

  def createNodeSourceOscilSine: NodeSourceOscilSine = {
    NodeSourceOscilSineScalajs(waCtx)
  }

  def createNodeSourceOscilSawtooth: NodeSourceOscilSine = {
    NodeSourceOscilSawtoothScalajs(waCtx)
  }

  def createNodeFilterGain: NodeFilterGain = {
    NodeFilterGainScalajs(waCtx)
  }

  def createNodeControlConstant(value: Double): NodeControlConstant = {
    NodeControlConstantScalajs(value)(waCtx)
  }

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double): NodeControlAdsr = {
    NodeControlAdsrScalajs(attack, decay, sustain, release)(waCtx)
  }

  def currentTime: Double = waCtx.currentTime

}

