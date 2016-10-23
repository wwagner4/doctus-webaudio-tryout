package doctus.sound

import akka.actor.{Actor, ActorRef, Props}
import ddf.minim.UGen
import ddf.minim.ugens.Constant

import scala.concurrent.duration._

case class NodeControlAdsrJvmMinim(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)(ctx: MinimContext) extends NodeControlEnvelope {

  private val adsrParams = AdsrParams(attack, decay, sustain, release, gain, trend)

  var ugenInput = Option.empty[UGen#UGenInput]

  var ugen = Option.empty[UGen]

  var adsrActor: ActorRef = ctx.actorSystem.actorOf(AdsrActor.props(ugen, ugenInput, adsrParams))

  /**
   * Starts the attack of the ADSR
   * @param time Time in seconds
   */
  def start(time: Double): Unit = {
    val func = () => {
      println(f"ADSR started at $time%.2f")
      adsrActor ! AdsrStartEvent
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr start")
    ctx.tell(me)
  }

  def stop(time: Double): Unit = {
    val func = () => {
      println(f"ADSR stopped at $time%.2f")
      adsrActor ! AdsrStopEvent
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr stop")
    ctx.tell(me)
  }

  def connect(param: ControlParam): Unit = {
    param match {
      case p: UGenInputAware =>
        ugenInput = Some(p.uGenInput)
        val _ugen = new Constant(0.0f)
        ugen = Some(_ugen)
        _ugen.patch(p.uGenInput)
        ugenInput = Some(p.uGenInput)
        println(s"connecting ADSR ${_ugen} to ${p.uGenInput} ($param)")

      case _ =>
        throw new IllegalArgumentException(
          s"cannot connect $this to $param. $param is not 'UGenAware'")
    }
  }
}


case object AdsrStartDecayEvent

case object AdsrStartSustainEvent

case object AdsrStartEvent

case object AdsrStopEvent

object AdsrActor {

  def props(ugen: Option[UGen], ugenInput: Option[UGen#UGenInput], adsrParams: AdsrParams): Props = Props(classOf[AdsrActor], ugen, ugenInput, adsrParams)

}

case class AdsrParams(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)

case class AdsrMsg(time: Double)

class AdsrActor(ugen: Option[UGen], ugenInput: Option[UGen#UGenInput],adsrParams: AdsrParams) extends Actor {
  
  def receive: Receive = {

    case AdsrStartEvent =>
      println(f"[receive] AdsrStartEvent")

    case AdsrStopEvent =>
      println(f"[receive] AdsrStopEvent")

    case AdsrStartDecayEvent =>
      println(f"[receive] AdsrStartDecayEvent")

    case AdsrStartSustainEvent =>
      println(f"[receive] AdsrStartSustainEvent")

    case msg: Any =>
      println(s"[receive] ADSR unhandled $msg")
      this.unhandled(msg)
  }

  def attack: Receive = {

    case AdsrStartEvent =>
      println(f"[attack] AdsrStartEvent")

    case AdsrStopEvent =>
      println(f"[attack] AdsrStopEvent")

    case AdsrStartDecayEvent =>
      println(f"[attack] AdsrStartDecayEvent")

    case AdsrStartSustainEvent =>
      println(f"[attack] AdsrStartSustainEvent")

    case msg: Any =>
      println(s"[attack] ADSR unhandled $msg")
      this.unhandled(msg)
  }

  def decay: Receive = {

    case AdsrStartEvent =>
      println(f"[decay] AdsrStartEvent")

    case AdsrStopEvent =>
      println(f"[decay] AdsrStopEvent")

    case AdsrStartDecayEvent =>
      println(f"[decay] AdsrStartDecayEvent")

    case AdsrStartSustainEvent =>
      println(f"[decay] AdsrStartSustainEvent")

    case msg: Any =>
      println(s"[decay] ADSR unhandled $msg")
      this.unhandled(msg)
  }

  def release: Receive = {

    case AdsrStartEvent =>
      println(f"[release] AdsrStartEvent")

    case AdsrStopEvent =>
      println(f"[release] AdsrStopEvent")

    case AdsrStartDecayEvent =>
      println(f"[release] AdsrStartDecayEvent")

    case AdsrStartSustainEvent =>
      println(f"[release] AdsrStartSustainEvent")

    case msg: Any =>
      println(s"[release] ADSR unhandled $msg")
      this.unhandled(msg)
  }

}

object DbCalc {

  private val a = 1.122f
  private val lna = math.log(a)

  def lin2db(linval: Double): Double = {
    require(linval >= 0.0)
    if (linval <= 0.001) -60
    else math.log(linval) / lna
  }

  def db2lin(dbval: Double): Double = math.pow(a, dbval)

}