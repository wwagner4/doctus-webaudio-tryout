package net.entelijan

import java.util.Date

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}

/**
  * Tryout AKKA behaviour
  */
object BehaviourTryout extends App {

  val sys = ActorSystem()

  val behaveProps = Props[Behave]

  val behave = sys.actorOf(behaveProps)

  for (_ <- (1 to 10)) behave ! "HALLO"

  sys.terminate()
}

class Behave extends Actor {

  var strCount = 0

  override def receive: Receive = {
    case str: String =>
      strCount += 1
      println(s"received a string '$str' $strCount")
    case msg: Any =>
      println(s"unhandled '$msg'")
      this.unhandled(msg)

  }
}
