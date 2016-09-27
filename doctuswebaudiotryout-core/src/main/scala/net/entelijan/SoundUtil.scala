// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

object SoundUtil {

  def xyParams[A, B](x: Seq[A], y: Seq[B]): Nineth => (A, B) = {

    case N_00 => (x(0), y(0))
    case N_01 => (x(0), y(1))
    case N_02 => (x(0), y(2))

    case N_10 => (x(1), y(0))
    case N_11 => (x(1), y(1))
    case N_12 => (x(1), y(2))

    case N_20 => (x(2), y(0))
    case N_21 => (x(2), y(1))
    case N_22 => (x(2), y(2))

  }



}
