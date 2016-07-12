// Copyright (C) 2016 wolfgang wagner http://entelijan.net
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0

package doctus.sound

sealed trait Nineth

case object N_00 extends Nineth
case object N_01 extends Nineth
case object N_02 extends Nineth
case object N_10 extends Nineth
case object N_11 extends Nineth
case object N_12 extends Nineth
case object N_20 extends Nineth
case object N_21 extends Nineth
case object N_22 extends Nineth

/**
 * Experimental Sound API
 */
trait DoctusSound {

  def tinitusStart(): Unit

  def tinitusStop(): Unit

  def noiseWhiteStart(nineth: Nineth): Unit

  def noiseWhiteStop(): Unit

  def noisePinkStart(nineth: Nineth): Unit

  def noisePinkStop(): Unit

  def noiseBrownRedStart(nineth: Nineth): Unit

  def noiseBrownRedStop(): Unit

  def melodyStart(): Unit

  def adsrStart(nineth: Nineth): Unit

  def adsrStop(): Unit

  def metalStart(): Unit

  def metalStop(): Unit

}
