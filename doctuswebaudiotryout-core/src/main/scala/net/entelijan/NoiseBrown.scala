// Copyright (C) 2016 wolfgang wagner http://entelijan.net
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0

package net.entelijan

import scala.util.Random

case class NoiseBrown(sampleRate: Double) extends ValueSequence {

  val ran = Random
  val brownCutoffFreq = 100.0
  val brownAmpCorr = 6.2
  val dt = 1.0 / sampleRate
  val RC = 1.0 / (2.0 * math.Pi * brownCutoffFreq)
  val brownAlpha = dt / (RC + dt)
  var lastOutput = 0.001

  def nextValue: Double = {
    val white = 2.0 * ran.nextDouble() - 1.0
    val output = brownAlpha * white + (1 - brownAlpha) * lastOutput
    lastOutput = output
    output * brownAmpCorr
  }

}
