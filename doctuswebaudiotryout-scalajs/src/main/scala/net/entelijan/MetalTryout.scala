package net.entelijan

import doctus.sound._
import org.scalajs.dom._

case class MetalTryout(ctx: AudioContext) {

  def start(): Unit = println("metal start")
  
  def stop(): Unit = println("metal stop")

}