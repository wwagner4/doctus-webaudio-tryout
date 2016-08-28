package doctus.sound

import org.scalajs.dom.AudioContext

case class NodeSinkLineOutScalajs(waCtx: AudioContext) extends NodeSinkLineOut {

}

case class NodeFilterGainScalajs(waCtx: AudioContext) extends NodeFilterGain {

  val _gainParam = new ControlParam {


  }

  def connect(filter: NodeFilter): NodeSource = {
    println("connected %s to %s" format (this, filter))
    this
  }

  def connect(sink: NodeSink): Unit = {
    println("connected %s to %s" format (this, sink))
  }

  def gain: ControlParam = {
    _gainParam
  }
}

case class NodeSourceOscilSineScalajs(waCtx: AudioContext) extends NodeSourceOscilSine {

  def connect(filter: NodeFilter): NodeSource = {
    println("connected %s to %s" format (this, filter))
    this
  }

  def connect(sink: NodeSink): Unit = {
    println("connected %s to %s" format (this, sink))
  }

  def start(time: Double): Unit = {
    println("started %s at %3.2f" format (this, time))
  }

  def stop(time: Double): Unit = {
    println("stopped %s at %3.2f" format (this, time))
  }

  def frequency: ControlParam = {
    println("accessing param 'frequency' of %s" format this)
    new ControlParam() {}
  }

}

case class NodeControlConstantScalajs(value: Double)(waCtx: AudioContext) extends NodeControlConstant {

  def connect(param: ControlParam): Unit = {
    println("connected %s to ControlParam: %s" format (this, param))
  }

}

case class NodeControlAdsrScalajs(attack: Double, decay: Double, sustain: Double, release: Double)(waCtx: AudioContext) extends NodeControlAdsr {

  def connect(param: ControlParam): Unit = {
    println("connected control node adsr %s to %s" format(this, param))
  }

  def stop(time: Double): Unit = {
    println("stopped control node adsr at %.2f" format time)
  }

  def start(time: Double): Unit = {
    println("started control node adsr at %.2f" format time)
  }
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

