// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import scala.util.Random

/**
 * Provides a sequence for white noise.
 */
case object NoiseWhite extends ValueSequence {

  val ran = Random

  def nextValue: Double = ran.nextDouble() * 2.0 - 1.0

}
