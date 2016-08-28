package doctus.sound

case class NodeSinkLineOutScalajs() extends NodeSinkLineOut {

}

case class NodeFilterGainScalajs() extends NodeFilterGain {

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

case class NodeSourceOscilSineScalajs() extends NodeSourceOscilSine {

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

case class NodeControlConstantScalajs(value: Double) extends NodeControlConstant {

  def connect(param: ControlParam): Unit = {
    println("connected %s to ControlParam: %s" format (this, param))
  }

}

case class NodeControlAdsrScalajs(attack: Double, decay: Double, sustain: Double, release: Double) extends NodeControlAdsr {

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

object DoctusSoundAudioContextScalajs extends DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSinkLineOut = {
    NodeSinkLineOutScalajs()
  }

  def createNodeSourceOscilSine: NodeSourceOscilSine = {
    NodeSourceOscilSineScalajs()
  }

  def createNodeFilterGain: NodeFilterGain = {
    NodeFilterGainScalajs()
  }

  def createNodeControlConstant(value: Double): NodeControlConstant = {
    NodeControlConstantScalajs(value)
  }

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double): NodeControlAdsr = {
    NodeControlAdsrScalajs(attack, decay, sustain, release)
  }

  def currentTime: Double = System.currentTimeMillis().toDouble / 1000

}

