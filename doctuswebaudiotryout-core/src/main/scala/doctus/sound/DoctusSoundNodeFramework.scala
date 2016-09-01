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

  def connect(filter: NodeFilter): NodeSource

  def `>-`(filter: NodeFilter): NodeSource = connect(filter)

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
  * Examples: Gain control, filters, ...
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
  * An oscillator
  */
trait NodeSourceOscil extends NodeSource with StartStoppable {

  def frequency: ControlParam

}

/**
  * A nose generator
  */
trait NodeSourceNoise extends NodeSource with StartStoppable {}

/**
  * An envelope node
  */
trait NodeControlEnvelope extends NodeControl with StartStoppable {}

/**
  * A LFO node
  */
trait NodeControlLfo extends NodeControl with StartStoppable {}

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

  /**
    * The speakers (headphones) of your device
    */
  def createNodeSinkLineOut: NodeSink

  def createNodeSourceOscilSine: NodeSourceOscil

  def createNodeSourceOscilSawtooth: NodeSourceOscil

  def createNodeSourceNoiseWhite: NodeSourceNoise

  def createNodeSourceNoisePink: NodeSourceNoise

  def createNodeSourceNoiseBrown: NodeSourceNoise

  def createNodeFilterGain: NodeFilterGain

  /**
    *
    * @param value the constant value provided by the node
    * @return a new constant value parameter control
    */
  def createNodeControlConstant(value: Double): NodeControl

  /**
    * Envelope controlled by four parameters.
    * For details see: https://en.wikipedia.org/wiki/Synthesizer#Attack_Decay_Sustain_Release_.28ADSR.29_envelope
    *
    * @param attack time in seconds
    * @param decay time in seconds
    * @param sustain relative value between 0.0 and 1.0
    * @param release time in seconds
    * @return an new ADSR controller
    */
  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double): NodeControlEnvelope

  /**
    * Low frequency oscillator
    *
    * @param frequency of the LFO in Herz
    * @param amplitude of the LFO
    * @param offset of the LFO
    * @return a new LFO
    */
  def createNodeControlLfo(frequency: Double, amplitude: Double, offset: Double): NodeControlLfo

  def currentTime: Double

}

