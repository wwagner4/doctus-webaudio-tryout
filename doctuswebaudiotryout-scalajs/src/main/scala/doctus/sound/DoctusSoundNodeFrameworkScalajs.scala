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

case class NodeFilterGainScalajs(waCtx: AudioContext, initialGain: Double) extends NodeFilterGain with AudioNodeAware {

  val waGain = waCtx.createGain()
  waGain.gain.value = initialGain

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
        println("NodeFilterGainScalajs: connected waGain(%s) to waFilter(%s)" format (waGain, src))
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
        println("NodeFilterGainScalajs: connected waGain(%s) to waSink(%s)" format (waGain, src))
      }
      case _ => throw new IllegalStateException("sink %s is not AudioNodeAware" format sink)
    }
  }

  def gain: ControlParam = paramGain

  def audioNode: AudioNode = waGain
}

case class NodeSourceOscilSineScalajs(waCtx: AudioContext) extends NodeSourceOscilSine with AudioNodeAware {

  val waOscil = waCtx.createOscillator()

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
        println("NodeSourceOscilSineScalajs: connected waOscil(%s) to waFilter(%s)" format (waOscil, waFilter))
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
        println("NodeSourceOscilSineScalajs: connected waOscil(%s) to waSink(%s)" format (waOscil, waSink))
      }
      case _ => throw new IllegalStateException("sink %s is not AudioNodeAware" format sink)
    }
  }

  def start(time: Double): Unit = {
    waOscil.start(time)
    println("started %s at %3.2f" format(this, time))
  }

  def stop(time: Double): Unit = {
    waOscil.start(time)
    println("stopped %s at %3.2f" format(this, time))
  }

  def frequency: ControlParam = paramFrequency

  def audioNode: AudioNode = waOscil

}

case class NodeControlConstantScalajs(value: Double)(waCtx: AudioContext)
  extends NodeControlConstant with WebAudioParamHolder{

  def connect(param: ControlParam): Unit = {
    param match {
      case connectable: ConnectableParam => connectable.onConnect(this)
      case _ => throw new IllegalStateException("control pram %s is not connectable" format param)
    }
  }

  override def addAudioParam(waParam: AudioParam): Unit = {
    waParam.value = value
    println("NodeControlConstantScalajs: set value %.2f to %s" format(value, waParam))
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
    if (waParamList.isEmpty) {
      println("stopped control node adsr NO PARAMS !!!")
    } else {
      println("stopped control node adsr at %.2f" format time)
      waParamList.foreach { p =>
        println("stopped control node adsr for %s" format p)
        p.cancelScheduledValues(0)
        p.setValueAtTime(p.value, time)
        p.linearRampToValueAtTime(0.0, time + release)
      }
    }
  }

  def start(time: Double): Unit = {
    if (waParamList.isEmpty) {
      println("started control node adsr NO PARAMS !!!")
    } else {
      println("started control node adsr at %.2f" format time)
      waParamList.foreach { p =>
        println("started control node adsr for %s" format p)
        p.cancelScheduledValues(0)
        p.setValueAtTime(0, time)
        p.linearRampToValueAtTime(valMax, time + attack)
        p.linearRampToValueAtTime(sustain * valMax, time + attack + decay)
      }
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

  def createNodeFilterGain(initialGain: Double): NodeFilterGain = {
    NodeFilterGainScalajs(waCtx, initialGain)
  }

  def createNodeControlConstant(value: Double): NodeControlConstant = {
    NodeControlConstantScalajs(value)(waCtx)
  }

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double): NodeControlAdsr = {
    NodeControlAdsrScalajs(attack, decay, sustain, release)(waCtx)
  }

  def currentTime: Double = waCtx.currentTime

}

