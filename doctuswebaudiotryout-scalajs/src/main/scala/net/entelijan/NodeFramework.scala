package net.entelijan
/*
 * Interfaces defining the base functionality of sound nodes
 */

/**
  * Creates a sound signal
  * Examples: Sine generator, ...
  */
trait NodeSource {

  def connect(sink: NodeSink): Unit
  def `>-`(sink: NodeSink): Unit

  def connect(sink: NodeFilter): NodeSource
  def `>-`(sink: NodeFilter): NodeSource

}

/**
  * Consumes a sound signal.
  * Examples: Loudspeakes, Files, ...
  */
trait NodeSink {

}

/**
  * Consumes a sound signal, transforms it and
  * provides the transformed signal like a NodeSource
  * Examples: Gaincontrol, filters, ...
  */
trait NodeFilter extends NodeSource {

}

/**
  * A node that creates a control signal that might be plugged into a
  * ControlParam.
  * Examples: LFO, ADSR, ...
  */
trait NodeControlParam {

}

/**
  * Consumer of the output of a NodeControlParam.
  * Usually parts of Nodes.
  * Examples: Gain of a sine generator, cutoff frequency of a filter, ...
  */
trait ContolParam {

}

/**
  * Starts a task at a defined time
  */
trait Startable {

  def start(time: Double): Unit

}

/**
  * Starts and stops a task at a defined time
  */
trait StartStopable extends Startable {

  def stop(time: Double): Unit

}