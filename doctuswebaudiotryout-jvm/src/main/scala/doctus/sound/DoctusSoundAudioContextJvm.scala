package doctus.sound

/**
 * Created by wwagner4 on 17/09/16.
 */
case class DoctusSoundAudioContextJvm() extends DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSink = ???

  def createNodeSourceOscil(waveType: WaveType): NodeSourceOscil = ???

  def createNodeSourceNoise(noiseType: NoiseType): NodeSourceNoise = ???

  def createNodeThroughGain(initialGain: Double): NodeThroughGain = ???

  def createNodeThroughFilter(filterType: FilterType, initialFrequency: Double, initialQuality: Double): NodeThroughFilter = ???

  def createNodeThroughPan(initialPan: Double): NodeThroughPan = ???

  def createNodeThroughDelay(initialDelay: Double): NodeThroughDelay = ???

  def createNodeControlConstant(value: Double): NodeControl = ???

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double): NodeControlEnvelope = ???

  def createNodeControlLfo(waveType: WaveType, initialFrequency: Double, initialAmplitude: Double): NodeControlLfo = ???

  def currentTime: Double = ???
}
