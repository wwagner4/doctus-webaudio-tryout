// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._
import scala.util.Random

case class MetalTryout(ctx: AudioContext) {

  val freqs = List(111, 222, 333, 444, 555)
  val ran = Random

  val metal = NodeMetal(ctx)
  metal.frequency(ranFreq, 0)
  
  def ranFreq: Double = freqs(ran.nextInt(freqs.size))

  metal.nodeOut.connect(ctx.destination)

  def start(): Unit = {
    val time = ctx.currentTime
    metal.frequency(ranFreq, time)
    metal.start(time)
  }

  def stop(): Unit = metal.stop(ctx.currentTime)

}

case class NodeMetal(ctx: AudioContext) extends NodeOut with NodeStartStoppable {

  case class GainableOscil(gainVal: Double) extends NodeOut {

    val oscil = ctx.createOscillator()
    val gain = ctx.createGain()
    gain.gain.value = gainVal
    oscil.connect(gain)
    oscil.start(0)

    def nodeOut: AudioNode = gain
  }

  val logDecay = SoundUtil.logDecay(3)(_)

  def createGainableOscils(cnt: Int): List[GainableOscil] = {
    if (cnt == 0) {
      List.empty[GainableOscil]
    } else {
      val gain = logDecay(cnt)
      val go = GainableOscil(gain)
      go :: createGainableOscils(cnt - 1)
    }
  }

  val cnt = 6
  val oscils = createGainableOscils(cnt)
  val gain = ctx.createGain()
  oscils.foreach { x => x.nodeOut.connect(gain) }
  val adsr = NodeAdsr(ctx)
  adsr.valAttack = 0.001
  adsr.valSustain = 0.3
  adsr.valRelease = 2.0
  gain.connect(adsr.nodeIn)

  def nodeOut: AudioNode = adsr.nodeOut

  def frequency(freq: Double, time: Double): Unit = {
    val hs = SoundUtil.metalHarmonics(freq, cnt)
    //noinspection VariablePatternShadow
    oscils.zip(hs) foreach { case (o, f) => o.oscil.frequency.setValueAtTime(f, time)}
  }

  def start(time: Double): Unit = {
    adsr.start(time)
  }

  def stop(time: Double): Unit = {
    adsr.stop(time)
  }

}
