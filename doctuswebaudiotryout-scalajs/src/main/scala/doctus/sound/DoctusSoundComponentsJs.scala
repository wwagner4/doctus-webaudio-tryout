package doctus.sound

import org.scalajs.dom._


trait CustomNode extends CustomSourceNode {

  def in: AudioNode

}

trait CustomSourceNode {

  def out: AudioNode

  def start(time: Double): Unit

  def stop(time: Double): Unit

}

case class Tremolo(ctx: AudioContext) extends CustomNode {
  
    private val oscil = ctx.createOscillator()
    private val amplGain = ctx.createGain()
    private val inOutGain = ctx.createGain()
    // Amplitude
    amplGain.gain.value = 0.1
    // Offset Amplitude
    inOutGain.gain.value = 0.5
    oscil.frequency.value = 0.5

    oscil.connect(amplGain)
    // The output of the oscil is added to the value previously set by amplGain.gain.value
    amplGain.connect(inOutGain.gain)

    
    def propFrequency = oscil.frequency
		def propAmplitude = amplGain.gain
		def propAmplitudeOffset = inOutGain.gain
    
    def start(time: Double): Unit = oscil.start(time)

    def stop(time: Double): Unit = oscil.stop(time)

    override def in: AudioNode = inOutGain
    override def out: AudioNode = inOutGain
}

case class Adsr(ctx: AudioContext) extends CustomNode {

  var valAttack = 0.01
  var valDecay = 0.1
  var valSustain = 0.01
  var valRelease = 0.5

  val gain = ctx.createGain()
  gain.gain.setValueAtTime(0, 0)

  override def start(time: Double): Unit = {
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(0, time)
    gain.gain.linearRampToValueAtTime(1.0, time + valAttack)
    gain.gain.linearRampToValueAtTime(valSustain, time + valAttack + valDecay)
  }

  override def stop(time: Double): Unit = {
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(gain.gain.value, time)
    gain.gain.linearRampToValueAtTime(0.0, time + valRelease)
  }

  override def in: AudioNode = gain

  override def out: AudioNode = gain

}

