// Copyright (C) 2016 wolfgang wagner http://entelijan.net
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0

package doctus.sound

import org.scalajs.dom._

trait NodeInOut extends NodeOut {

  def nodeIn: AudioNode

}

trait NodeOut {

  def nodeOut: AudioNode

}

trait NodeStartStoppable {

  def start(time: Double): Unit

  def stop(time: Double): Unit

}

case class Tremolo(ctx: AudioContext) extends NodeInOut with NodeStartStoppable {

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

  def propFrequency: AudioParam = oscil.frequency
  def propAmplitude: AudioParam = amplGain.gain
  def propAmplitudeOffset: AudioParam = inOutGain.gain

  def start(time: Double): Unit = oscil.start(time)

  def stop(time: Double): Unit = oscil.stop(time)

  def nodeIn: AudioNode = inOutGain
  def nodeOut: AudioNode = inOutGain
}

case class Adsr(ctx: AudioContext) extends NodeInOut with NodeStartStoppable {

  var valAttack = 0.01
  var valDecay = 0.1
  var valSustain = 0.01
  var valRelease = 0.5

  private val gain = ctx.createGain()
  gain.gain.setValueAtTime(0, 0)

  def start(time: Double): Unit = {
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(0, time)
    gain.gain.linearRampToValueAtTime(1.0, time + valAttack)
    gain.gain.linearRampToValueAtTime(valSustain, time + valAttack + valDecay)
  }

  def stop(time: Double): Unit = {
    gain.gain.cancelScheduledValues(0)
    gain.gain.setValueAtTime(gain.gain.value, time)
    gain.gain.linearRampToValueAtTime(0.0, time + valRelease)
  }

  def nodeIn: AudioNode = gain
  def nodeOut: AudioNode = gain

}

