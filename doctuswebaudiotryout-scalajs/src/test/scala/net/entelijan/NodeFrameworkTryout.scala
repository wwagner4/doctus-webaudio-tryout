package net.entelijan

/**
 * Experimenting with the NodeFramework
 */
object NodeFrameworkTryout extends App {

  val ctx = AudioContextScalajs

  def a: Unit = {
    val src = ctx.createNodeSourceOscilSine
    val sink = ctx.createNodeSinkLineOut

    src >- sink
  }

  def b: Unit = {
    val src = ctx.createNodeSourceOscilSine
    val filter = ctx.createNodeFilterGain
    val sink = ctx.createNodeSinkLineOut

    src >- filter >- sink
  }

  def c: Unit = {
    val src1 = ctx.createNodeSourceOscilSine
    val src2 = ctx.createNodeSourceOscilSine
    val filter = ctx.createNodeFilterGain
    val sink = ctx.createNodeSinkLineOut

    src1 >- filter >- sink
    src2 >- filter
  }

  def d: Unit = {
    val src = ctx.createNodeSourceOscilSine
    val freq = ctx.createNodeControlConstant
    freq.value = 444
    val sink = ctx.createNodeSinkLineOut

    freq.connect(src.frequency)
    src >- sink

    val now = ctx.currentTime
    src.start(now)
    src.stop(now + 10)

  }

  d

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

  object AudioContextScalajs extends AudioContext {

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

}
