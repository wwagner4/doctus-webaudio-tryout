package net.entelijan

/**
  * Experimenting with the NodeFramework
  */
object NodeFrameworkTryout extends App {

  val ctx = NodeContext

  def a: Unit = {
    val src = ctx.createNodeSource
    val sink = ctx.createNodeSink

    src.connect(sink)
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

    src1.connect(filter).connect(sink)
    src2.connect(filter)
  }

  object NodeContext {

    def createNodeSource: NodeSource = ???

    def createNodeSink: NodeSink = ???

    def createNodeFilter: NodeFilter = ???

    def currentTime: Double = ???

  }

}
