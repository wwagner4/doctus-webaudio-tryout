package doctus.sound

/**
 * Jvm implementation of the DoctusSoundAudioContext
 */
//noinspection NotImplementedCode
case class DoctusSoundAudioContextJvmMinim() extends DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSink = ???

  def createNodeSourceOscil(waveType: WaveType): NodeSourceOscil = ???

  def createNodeSourceNoise(noiseType: NoiseType): NodeSourceNoise = ???

  def createNodeThroughGain: NodeThroughGain = ???

  def createNodeThroughFilter(filterType: FilterType): NodeThroughFilter = ???

  def createNodeThroughPan: NodeThroughPan = ???

  def createNodeThroughDelay: NodeThroughDelay = ???

  def createNodeControlConstant(value: Double): NodeControl = ???

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend): NodeControlEnvelope = ???

  def createNodeControlLfo(waveType: WaveType): NodeControlLfo = ???

  def currentTime: Double = ???

  def sampleRate: Double = ???

}
