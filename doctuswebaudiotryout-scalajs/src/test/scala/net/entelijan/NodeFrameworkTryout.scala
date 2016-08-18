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

  object NodeContext {

    def createNodeSource: NodeSource = ???

    def createNodeSink: NodeSink = ???

  }

}
