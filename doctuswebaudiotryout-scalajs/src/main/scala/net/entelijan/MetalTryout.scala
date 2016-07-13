// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.sound._
import org.scalajs.dom._

case class MetalTryout(ctx: AudioContext) {

  def start(): Unit = println("metal start")

  def stop(): Unit = println("metal stop")

}

case class NodeMetal(ctx: AudioContext) extends NodeOut with NodeStartStoppable {

  def nodeOut: AudioNode = ???

  def start(time: Double): Unit = ???

  def stop(time: Double): Unit = ???

}
