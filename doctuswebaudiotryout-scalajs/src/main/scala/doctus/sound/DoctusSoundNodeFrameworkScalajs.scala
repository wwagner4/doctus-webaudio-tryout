package doctus.sound

import org.scalajs.dom.{AudioContext, AudioParam}

trait ConnectableParam {

  def onConnect: NodeControl => Unit

}

trait WebAudioParamHolder {

  def addAudioParam(waParam: AudioParam)

}

case class NodeSinkLineOutScalajs(waCtx: AudioContext) extends NodeSinkLineOut {

}

case class NodeFilterGainScalajs(waCtx: AudioContext) extends NodeFilterGain {

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
    println("connected %s to %s" format(this, filter))
    this
  }

  def connect(sink: NodeSink): Unit = {
    println("connected %s to %s" format(this, sink))
  }

  def gain: ControlParam = {
    gainParam
  }
}

case class NodeSourceOscilSineScalajs(waCtx: AudioContext) extends NodeSourceOscilSine {

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
    println("connected %s to %s" format(this, filter))
    this
  }

  def connect(sink: NodeSink): Unit = {
    println("connected %s to %s" format(this, sink))
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

