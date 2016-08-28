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

case class NodeControlConstantScalajs() extends NodeControlConstant {

  var _value = 1.0

  def value: Double = {
    println("get 'value' = %s on %s" format (_value, this))
    _value
  }

  def value_=(v: Double): Unit = {
    println("set 'value' = %.2f on %s" format (v, this))
    _value = v
  }

  def connect(param: ControlParam): Unit = {
    println("connected %s to ControlParam: %s" format (this, param))
  }

}

case class NodeControlAdsrScalajs() extends NodeControlAdsr {

  var _attack = 1.0
  var _decay = 1.0
  var _sustain = 0.5
  var _release = 1.0

  def attack: Double = _attack

  def attack_=(v: Double): Unit = {
    require(v >= 0.0, "attack value (%.2f) must be >= 0.0" format v)
    _attack = v
    println("set attack to %.2f" format v)
  }

  def decay: Double = _decay

  def decay_=(v: Double): Unit = {
    require(v >= 0.0, "decay value (%.2f) must be >= 0.0" format v)
    _decay = v
    println("set decay to %.2f" format v)
  }

  def sustain: Double = _sustain

  def sustain_=(v: Double): Unit = {
    require(v >= 0.0 && v <= 1.0, "sustain value (%.2f) must be >= 0.0 and <= 1.0" format v)
    _sustain = v
    println("set sustain to %.2f" format v)
  }

  def release: Double = _release

  def release_=(v: Double): Unit = {
    require(v >= 0.0, "decay value (%.2f) must be >= 0.0" format v)
    _release = v
    println("set release to %.2f" format v)
  }

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

  def createNodeControlConstant: NodeControlConstant = {
    NodeControlConstantScalajs()
  }

  def createNodeControlAdsr: NodeControlAdsr = {
    NodeControlAdsrScalajs()
  }

  def currentTime: Double = System.currentTimeMillis().toDouble / 1000

}

