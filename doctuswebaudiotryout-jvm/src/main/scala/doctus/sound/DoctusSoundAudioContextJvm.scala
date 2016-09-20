package doctus.sound

/**
  * Created by wwagner4 on 17/09/16.
  */
case class DoctusSoundAudioContextJvm() extends DoctusSoundAudioContext {

  def createNodeSinkLineOut: NodeSink = ???

  def createNodeSourceOscil(waveType: WaveType): NodeSourceOscil = ???

  def createNodeSourceNoise(noiseType: NoiseType): NodeSourceNoise = ???

  def createNodeThroughGain: NodeThroughGain = ???

  def createNodeThroughFilter(filterType: FilterType): NodeThroughFilter = ???
  
  def createNodeThroughPan: NodeThroughPan = ???

  def createNodeControlConstant(value: Double): NodeControl = ???

  def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double): NodeControlEnvelope = ???

  def createNodeControlLfo(waveType: WaveType, frequency: Double, amplitude: Double): NodeControlLfo = ???

  def currentTime: Double = ???
}
