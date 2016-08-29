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

  val gainParam = new ConnectableParam with ControlParam {

    def onConnect = nodeControl => {
      println("connected control node %s to gain param" format nodeControl)
      nodeControl match {
        case holder: WebAudioParamHolder => holder.addAudioParam(waGain.gain)
        case _ => throw new IllegalStateException("control node %s is not a WebAudioParamHolder" format nodeControl)
      }
    }

  }

  def connect(filter: NodeFilter): NodeSource = {
    println("connecting %s to %s" format(this, filter))
    filter match {
      case node: AudioNodeAware => waGain.connect(node.audioNode)
      case _ => throw new IllegalStateException("filter %s is not AudioNodeAware" format filter)
    }
    this
  }

  def connect(sink: NodeSink): Unit = {
    println("connecting %s to %s" format(this, sink))
    sink match {
      case node: AudioNodeAware => waGain.connect(node.audioNode)
      case _ => throw new IllegalStateException("sink %s is not AudioNodeAware" format sink)
    }
  }

  def gain: ControlParam = gainParam


  def audioNode: AudioNode = waGain
}

case class NodeSourceOscilSineScalajs(waCtx: AudioContext) extends NodeSourceOscilSine with AudioNodeAware {

  val waOscil = waCtx.createOscillator()

  val paramFrequency = new ControlParam with ConnectableParam {
    def onConnect: (NodeControl) => Unit = nodeControl => {
      nodeControl match {
        case holder: WebAudioParamHolder => holder.addAudioParam(waOscil.frequency)
        case _ => throw new IllegalStateException("control node %s is not a WebAudioParamHolder" format nodeControl)
      }
    }
  }

  def connect(filter: NodeFilter): NodeSource = {
    println("connecting %s to %s" format(this, filter))
    filter match {
      case node: AudioNodeAware => waOscil.connect(node.audioNode)
      case _ => throw new IllegalStateException("filter %s is not AudioNodeAware" format filter)
    }
    this
  }

  def connect(sink: NodeSink): Unit = {
    println("connecting %s to %s" format(this, sink))
    sink match {
      case node: AudioNodeAware => waOscil.connect(node.audioNode)
      case _ => throw new IllegalStateException("sink %s is not AudioNodeAware" format sink)
    }
  }

  def start(time: Double): Unit = {
    println("started %s at %3.2f" format(this, time))
  }

  def stop(time: Double): Unit = {
    println("stopped %s at %3.2f" format(this, time))
  }

  def frequency: ControlParam = {
    println("accessing param 'frequency' of %s" format this)
    paramFrequency
  }

  def audioNode: AudioNode = waOscil

}

case class NodeControlConstantScalajs(value: Double)(waCtx: AudioContext)
  extends NodeControlConstant with WebAudioParamHolder{

  def connect(param: ControlParam): Unit = {
    println("connecting %s to ControlParam: %s" format(this, param))
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ => throw new IllegalStateException("control pram %s is not connectable" format param)
    }
  }

  override def addAudioParam(waParam: AudioParam): Unit = waParam.value = value

}

case class NodeControlAdsrScalajs(attack: Double, decay: Double, sustain: Double, release: Double)(waCtx: AudioContext)
  extends NodeControlAdsr with WebAudioParamHolder {

  val valMax = 1.0

  var waParamList = List.empty[AudioParam]

  def connect(param: ControlParam): Unit = {
    println("connecting control node adsr %s to %s" format(this, param))
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ => throw new IllegalStateException("control pram %s is not connectable" format param)
    }
  }

  def stop(time: Double): Unit = {
    println("stopped control node adsr at %.2f" format time)
    waParamList.foreach { p =>
      p.cancelScheduledValues(0)
      p.setValueAtTime(p.value, time)
      p.linearRampToValueAtTime(0.0, time + release)
    }
  }

  def start(time: Double): Unit = {
    println("started control node adsr at %.2f" format time)
    waParamList.foreach { p =>
      p.cancelScheduledValues(0)
      p.setValueAtTime(0, time)
      p.linearRampToValueAtTime(valMax, time + attack)
      p.linearRampToValueAtTime(sustain * valMax, time + attack + decay)
    }
  }

  def addAudioParam(waParam: AudioParam): Unit = waParamList ::= waParam
}

case class DoctusSoundAudioContextScalajs(waCtx: AudioContext) extends DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSinkLineOut = {
    NodeSinkLineOutScalajs(waCtx)
  }

  def createNodeSourceOscilSine: NodeSourceOscilSine = {
    NodeSourceOscilSineScalajs(waCtx)
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

