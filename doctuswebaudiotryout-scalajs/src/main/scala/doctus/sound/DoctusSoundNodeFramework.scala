package doctus.sound

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

  def connect(param: ControlParam): Unit

  def `>-`(param: ControlParam): Unit = connect(param)

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
  * Provides always a constant value.
  */
trait NodeControlConstant extends NodeControl {

  /**
    * @return The constant value provided
    *         Default: 1.0
    */
  def value: Double

  def value_=(v: Double): Unit

}


/**
  * Envelope generater controlled by four parameters.
  * For details see: https://en.wikipedia.org/wiki/Synthesizer#Attack_Decay_Sustain_Release_.28ADSR.29_envelope
  */
trait NodeControlAdsr extends NodeControl with StartStoppable {

  /**
    * Attack time in seconds.
    * Range: >= 0.0
    * Default: 1.0 second
    */
  def attack_=(v: Double): Unit

  def attack: Double

  /**
    * Decay time in seconds.
    * Range: >= 0.0
    * Default: 1.0 second
    */
  def decay_=(v: Double): Unit

  def decay: Double

  /**
    * Sustain level relative to the 1.0.
    * Range: 0.0 <= sustain <= 1.0
    * Default: 0.5
    */
  def sustain_=(v: Double): Unit

  def sustain: Double

  /**
    * Release time in seconds.
    * Range: >= 0.0
    * Default: 1.0
    */
  def release_=(v: Double): Unit

  def release: Double

}

/**
  * Component for gain control
  */
trait NodeFilterGain extends NodeFilter {

  def gain: ControlParam

}



/**
  * Creates audio components and provides auxiliary functions
  */
trait DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSinkLineOut

  def createNodeSourceOscilSine: NodeSourceOscilSine

  def createNodeFilterGain: NodeFilterGain

  def createNodeControlConstant: NodeControlConstant

  def createNodeControlAdsr: NodeControlAdsr

  def currentTime: Double

}

