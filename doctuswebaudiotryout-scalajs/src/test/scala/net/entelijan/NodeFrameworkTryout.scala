package net.entelijan

/**
 * Experimenting with the NodeFramework
 */
object NodeFrameworkTryout extends App {

  val ctx = NodeContext

  def a: Unit = {
    val src = ctx.createNodeSource
    val sink = ctx.createNodeSink

    src >- sink
  }

  def b: Unit = {
    val src = ctx.createNodeSource
    val filter = ctx.createNodeFilter
    val sink = ctx.createNodeSink

    src.connect(filter).connect(sink)
  }

  def c: Unit = {
    val src1 = ctx.createNodeSource
    val src2 = ctx.createNodeSource
    val filter = ctx.createNodeFilter
    val sink = ctx.createNodeSink

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

  class NodeSinkLineOut extends NodeSink {

  }

  class NodeSourceOscilSine extends NodeSource with StartStopable {

    def `>-`(filter: NodeFilter): NodeSource = connect(filter)
    
    def connect(filter: NodeFilter): NodeSource = {
      println("connected %s to %s" format (this, filter))
      this
    }

    def `>-`(sink: NodeSink): Unit = connect(sink)
    
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

  object NodeContext {

    def createNodeSource: NodeSource = ???

    def createNodeSink: NodeSink = ???

    def createNodeFilter: NodeFilter = ???

    def createNodeSinkLineOut: NodeSinkLineOut = {
      new NodeSinkLineOut()
    }

    def createNodeSourceOscilSine: NodeSourceOscilSine = {
      new NodeSourceOscilSine()
    }

    def currentTime: Double = System.currentTimeMillis().toDouble / 1000

  }

}
