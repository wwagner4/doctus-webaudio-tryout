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
  
  def `>-`(sink: NodeSink): Unit = connect(sink)

  def connect(sink: NodeFilter): NodeSource

  def `>-`(sink: NodeFilter): NodeSource = connect(sink)

}

/**
 * Consumes a sound signal.
 * Examples: Loudspeakers, Files, ...
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
 * A node that creating a signal that might be plugged into a
 * ControlParam.
 * Examples: LFO, ADSR, ...
 */
trait NodeControl {

}

/**
 * Consumer of the output of a NodeControlParam.
 * Usually parts of Nodes.
 * Examples: Gain of a sine generator, cutoff frequency of a filter, ...
 */
trait ControlParam {

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
trait StartStoppable extends Startable {

  def stop(time: Double): Unit

}

/**
 * The speakers (headphones) of your device
 */
trait NodeSinkLineOut extends NodeSink {

}

/**
 * A sine generator
 */
trait NodeSourceOscilSine extends NodeSource with StartStoppable {
  
  def frequency: ControlParam 

}

/**
 * Component for gain control
 */
trait NodeFilterGain extends NodeFilter {

}



/**
 * Creates audio components and provides auxiliary functions
 */
trait AudioContext {

  def createNodeSinkLineOut: NodeSinkLineOut

  def createNodeSourceOscilSine: NodeSourceOscilSine

  def createNodeFilterGain: NodeFilter

  /**
   * The current time in seconds
   */
  def currentTime: Double

}

