package net.entelijan

import scala.util.Random

/**
  * Provides a sequence for white noise.
  */
case object NoiseWhite extends ValueSequence {

  println("Noise White")

  val ran = Random

  def nextValue: Double = ran.nextDouble() * 2.0 - 1.0

}
