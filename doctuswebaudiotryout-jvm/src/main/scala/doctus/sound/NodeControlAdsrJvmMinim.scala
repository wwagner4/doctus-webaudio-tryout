package doctus.sound

import akka.actor.{Actor, ActorRef, Props}
import ddf.minim.ugens.Constant

case class NodeControlAdsrJvmMinim(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)
                                  (ctx: MinimContext) extends NodeControlEnvelope {

  import scala.concurrent.duration._

  private val minimConst = new Constant(-60f)

  var adsrActorOpt = Option.empty[ActorRef]

  val f = () => {
    adsrActorOpt.foreach(r => r ! AdsrTimeEvent(ctx.currentTime))
  }

  val scheduler = ctx.actorSystem.scheduler.schedule(0.second, 7812.micro)(f())(ctx.actorSystem.dispatcher)

  def start(time: Double): Unit = {
    val func = () => {
      println(f"ADSR started at $time%.2f")
      adsrActorOpt = Some(ctx.actorSystem.actorOf(AdsrActor.props(minimConst)))
      ()
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr start")
    ctx.tell(me)
  }

  def stop(time: Double): Unit = {
    val func = () => {
      println(f"ADSR stopped at $time%.2f")
      adsrActorOpt.foreach(r => r ! AdsrStopEvent)
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr stop")
    ctx.tell(me)
  }

  def connect(param: ControlParam): Unit = {
    param match {
      case p: UGenInputAware =>
        println(s"connecting ADSR $minimConst to ${p.uGenInput} ($param)")
        minimConst.patch(p.uGenInput)
        ()
      case _ =>
        throw new IllegalArgumentException(
          s"cannot connect $this to $param. $param is not 'UGenAware'")
    }
  }
}

case class AdsrTimeEvent(time: Double)

case object AdsrStopEvent

object AdsrActor {

  def props(const: Constant): Props = Props(classOf[AdsrActor], const)

}

case class AdsrMsg(time: Double)

class AdsrActor(const: Constant) extends Actor {

  var value = -60f

  def receive: Receive = {

    case AdsrTimeEvent(time) =>
      value += 0.1f
      const.setConstant(value)
//      println(s"received ADSR time event $time")

    case AdsrStopEvent =>
      println(s"received ADSR stop event $const")
      context.become(stopped)

    case msg: Any => {
      println(s"AdsrActor $msg")
      this.unhandled(msg)
    }
  }
  def stopped: Receive = {
    case msg: Any => {
      println(s"Stopped AdsrActor $msg $value")
      this.unhandled(msg)
    }

  }
}

