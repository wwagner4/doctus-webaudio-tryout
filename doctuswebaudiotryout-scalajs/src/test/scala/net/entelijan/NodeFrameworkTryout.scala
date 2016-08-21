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
    val sink = ctx.createNodeSinkLineOut

    src >- sink

    val now = ctx.currentTime
    src.start(now)
    src.stop(now + 10)

  }

  d

  case object NodeSinkLineOutScalajs extends NodeSinkLineOut {

  }

  case object NodeFilterGainScalajs extends NodeFilterGain {

    def connect(filter: NodeFilter): NodeSource = {
      println("connected %s to %s" format (this, filter))
      this
    }

    def connect(sink: NodeSink): Unit = {
      println("connected %s to %s" format (this, sink))
    }

  }

  case object NodeSourceOscilSineScalajs extends NodeSourceOscilSine {

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

  }

  object AudioContextScalajs extends AudioContext {

    def createNodeSinkLineOut: NodeSinkLineOut = {
      NodeSinkLineOutScalajs
    }

    def createNodeSourceOscilSine: NodeSourceOscilSine = {
      NodeSourceOscilSineScalajs
    }

    def createNodeFilterGain: NodeFilterGain = {
      NodeFilterGainScalajs
    }

    def currentTime: Double = System.currentTimeMillis().toDouble / 1000

  }

}
