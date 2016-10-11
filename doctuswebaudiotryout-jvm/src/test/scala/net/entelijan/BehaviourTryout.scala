package net.entelijan

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Tryout AKKA behaviour
  */
object BehaviourTryout extends App {

  val sys = ActorSystem()

  val behaveProps = Props[Behave]

  val behave = sys.actorOf(behaveProps)

  for (i <- 1 to 10) behave ! f"--$i%04d--"

  sys.terminate()
}

class Behave extends Actor {
  import context._

  var strCount = 0

  def receive: Receive = {
    case str: String =>
      strCount += 1
      if (strCount > 2) become(fastCount)
      println(s"[receive] received a string '$str' $strCount")
    case msg: Any =>
      println(s"[receive] unhandled '$msg'")
      this.unhandled(msg)

  }

  def fastCount: Receive = {
    case str: String =>
      strCount += 10
      if (strCount > 40) become(receive)
      println(s"[fast Count] received a string '$str' $strCount")
    case msg: Any =>
      println(s"[fast Count] unhandled '$msg'")
      this.unhandled(msg)

  }
}
