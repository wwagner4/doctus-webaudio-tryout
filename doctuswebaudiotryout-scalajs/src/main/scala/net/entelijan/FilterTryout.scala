// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

case class FilterTryout(ctx: AudioContext) {

  val oscil = ctx.createOscillator()
  oscil.`type` = "sawtooth"
  oscil.start(0)
  val adsr = NodeAdsr(ctx)

  oscil.connect(adsr.nodeIn)
  adsr.nodeOut.connect(ctx.destination)

  def start(): Unit = {
    val t = ctx.currentTime
    adsr.start(t)
  }

  def stop(): Unit = {
    val t = ctx.currentTime
    adsr.stop(t)
  }

}