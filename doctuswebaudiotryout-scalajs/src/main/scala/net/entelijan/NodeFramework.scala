package net.entelijan
/*
 * Interfaces defining the base functionallity of sound nodes
 */

/**
  * Creates a sound signal
  * Examples: Sine generator, ...
  */
trait NodeSource {
  def connect(sink: NodeSink): Unit;
}

/**
  * Consumes a sound signal.
  * Examples: Loudspeakes, Files, ...
  */
trait NodeSink {

}

/**
  * Consumes a sound signal, transformes it and
  * provides the transfored signal like a NodeSource
  * Examples: Gaincontrol, filters, ...
  */
trait NodeFilter {

}

/**
  * A node that creates a control signal that ight be plugged into a
  * ControlParam.
  * Examples: LFO, ADSR, ...
  */
trait NodeControlParam {

}

/**
  * Consumer of the otput of a NodeControlParam.
  * Usually parts of Nodes.
  * Examples: Gain of a sine generator, cutoff frequency of a filter, ...
  */
trait ContolParam {

}