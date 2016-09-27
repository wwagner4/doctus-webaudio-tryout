package doctus.sound

/*
 * Interfaces defining the base functionality of sound nodes
 */

sealed trait WaveType

case object WaveType_Sine extends WaveType
case object WaveType_Triangle extends WaveType
case object WaveType_Sawtooth extends WaveType
case object WaveType_Square extends WaveType

sealed trait NoiseType

case object NoiseType_White extends NoiseType
case object NoiseType_Pink extends NoiseType
case object NoiseType_Red extends NoiseType
case object NoiseType_Brown extends NoiseType

sealed trait FilterType
case object FilterType_Lowpass extends FilterType
case object FilterType_Highpass extends FilterType
case object FilterType_Bandpass extends FilterType

/**
  * Creates a sound signal
  * Examples: Sine generator, ...
  */
trait NodeSource {

  def connect(sink: NodeSink): Unit

  def `>-`(sink: NodeSink): Unit = connect(sink)

  def connect(through: NodeThrough): NodeSource

  def `>-`(through: NodeThrough): NodeSource = connect(through)

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
trait NodeThrough extends NodeSource with NodeSink {

}

/**
  * Frequency filter 
  */
trait NodeThroughFilter extends NodeThrough {

  def frequency: ControlParam

  def quality: ControlParam

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
  * Starts and stops a task at a defined time
  */
trait StartStoppable {

  def start(time: Double): Unit

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
trait NodeControlLfo extends NodeControl with StartStoppable {

  /**
    * @return control parameter for the frequency of the LFO
    */
  def frequency: ControlParam

  /**
    * @return control parameter for the amplitude of the LFO
    */
  def amplitude: ControlParam

}

/**
  * Component for gain control
  */
trait NodeThroughGain extends NodeThrough {

  def gain: ControlParam

}

/**
  * Component for panning control
  */
trait NodeThroughPan extends NodeThrough {

  /**
   * Takes values between -1 and +1 to control how
   * the sound signal is split between the right and the left channel.
   */
  def pan: ControlParam

}

/**
  * Component for panning control
  */
trait NodeThroughDelay extends NodeThrough {

  /**
   * The time the sound signal is delayed in seconds
   */
  def delay: ControlParam

}

trait NodeThroughContainer extends NodeThrough {

  def source: NodeSource

  def sink: NodeSink

  def connect(sink: NodeSink): Unit = source.connect(sink)

  def connect(through: NodeThrough): NodeSource = source.connect(through)

}

trait NodeSourceContainer extends NodeSource {

  def source: NodeSource

  def connect(sink: NodeSink): Unit = source.connect(sink)

  def connect(through: NodeThrough): NodeSource = source.connect(through)

}

/**
  * Creates audio components and provides auxiliary functions
  */
trait DoctusSoundAudioContext {

  /**
    * The speakers (headphones) of your device
    */
  def createNodeSinkLineOut: NodeSink

  /**
   * Creates an oscillator with a certain wave form.
   * The wave form might be 'sine', 'sawtooth', ... . @see WaveType
   */
  def createNodeSourceOscil(waveType: WaveType): NodeSourceOscil

  /**
   * Creates a noise signal of a certain characteristic.
   * The characteristic might be 'brown noise', 'white noise', ... . @see NoiseType
   */
  def createNodeSourceNoise(noiseType: NoiseType): NodeSourceNoise

  /**
   * Creates a node for controlling the gain of
   * a sound signal. @see NodeThroughGain
   */
  def createNodeThroughGain: NodeThroughGain

  /**
   * Creates a sound filter of a certain characteristic.
   * The characteristic might be 'lowpass', 'highpass', ... . @see FilterType
   */
  def createNodeThroughFilter(filterType: FilterType): NodeThroughFilter
  
  /**
   * Creates a panning node. @see NodeThroughPan
   */
  def createNodeThroughPan: NodeThroughPan
  
  /**
   * Creates a delay node. @see NodeThroughDelay
   */
  def createNodeThroughDelay: NodeThroughDelay

  /**
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
  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double = 1.0): NodeControlEnvelope

  /**
    * Low frequency oscillator
    *
    * @param waveType Type of the LFOs wave (sine, triangle, ...)
    * @return a new LFO
    */
  def createNodeControlLfo(waveType: WaveType): NodeControlLfo

  /**
   * The current time of the underlaying sound system in seconds.
   */
  def currentTime: Double

  /**
    * @return the sample rate of the underlaying sound system
    */
  def sampleRate: Double

}

