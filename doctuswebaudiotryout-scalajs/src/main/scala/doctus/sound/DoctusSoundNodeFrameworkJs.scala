package doctus.sound

case class NodeSinkLineOutScalajs() extends NodeSinkLineOut {

}

case class NodeFilterGainScalajs() extends NodeFilterGain {

  def connect(filter: NodeFilter): NodeSource = {
    println("connected %s to %s" format (this, filter))
    this
  }

  def connect(sink: NodeSink): Unit = {
    println("connected %s to %s" format (this, sink))
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

case class NodeControlConstantScalajs() extends NodeControlConstant {

  var _value = Option.empty[Double]

  def value: Double = {
    println("get 'value' = %s on %s" format (_value, this))
    _value.get
  }

  def value_=(v: Double): Unit = {
    println("set 'value' = %.2f on %s" format (v, this))
    _value = Some(v)
  }

  def connect(param: ControlParam): Unit = {
    println("connected %s to ControlParam: %s" format (this, param))
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

  def createNodeControlConstant: NodeControlConstant = {
    NodeControlConstantScalajs()
  }

  def currentTime: Double = System.currentTimeMillis().toDouble / 1000

}

