package doctus.sound

import akka.actor.{Actor, ActorRef, Props}
import ddf.minim.ugens.Constant
import scala.concurrent.duration._

object AdsrConstants {

  val rate = 7812.micro
  val rateSeconds: Double = rate.toMicros * 1.0e-9
  val zeroDb = -60.0

}


case class NodeControlAdsrJvmMinim(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)
                                  (ctx: MinimContext) extends NodeControlEnvelope {

  private val minimConst = new Constant(-60f)

  private val adsrParams = AdsrParams(attack, decay, sustain, release, gain, trend)

  var adsrActor: ActorRef = ctx.actorSystem.actorOf(AdsrActor.props(minimConst, adsrParams))

  val f = () => {
    adsrActor ! AdsrTimeEvent(ctx.currentTime)
  }

  val scheduler = ctx.actorSystem.scheduler.schedule(0.second, AdsrConstants.rate)(f())(ctx.actorSystem.dispatcher)

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

case object AdsrStartEvent

case object AdsrStopEvent

object AdsrActor {

  def props(const: Constant, adsrParams: AdsrParams): Props = Props(classOf[AdsrActor], const, adsrParams)

}

case class AdsrParams(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)

case class AdsrMsg(time: Double)

class AdsrActor(const: Constant, adsrParams: AdsrParams) extends Actor {

  var cnt = 0
  val logFreq = 50

  var value = AdsrConstants.zeroDb
  var diffValue = 0.0

  var time: Integer = 0
  var targetTime: Integer = 0

  val gainDb = DbCalc.lin2db(adsrParams.gain.toFloat)
  val sustainDb = DbCalc.lin2db(adsrParams.gain.toFloat)

  def receive: Receive = {
    case AdsrTimeEvent(_) =>
      cnt += 1
      if (cnt % logFreq == 0) println(s"[receive] ADSR time event $value")
    // Nothing to do here

    case AdsrStartEvent =>
      println(s"[receive] ADSR start event $value")
      time = 0
      targetTime = (adsrParams.attack / AdsrConstants.rateSeconds).toInt
      diffValue = (gainDb - value) / targetTime
      context.become(attack)

    case AdsrStopEvent =>
      println(s"[receive] ADSR stop event $value")
      time = 0
      targetTime = (adsrParams.release / AdsrConstants.rateSeconds).toInt
      diffValue = (AdsrConstants.zeroDb - value) / targetTime
      context.become(release)

    case msg: Any => {
      println(s"[receive] ADSR unhandled $msg")
      this.unhandled(msg)
    }
  }

  def attack: Receive = {
    case AdsrTimeEvent(_) =>
      cnt += 1
      if (cnt % logFreq == 0) println(s"[attack] ADSR time event $value")
      if (time > targetTime) {
        time = 0
        targetTime = (adsrParams.decay / AdsrConstants.rateSeconds).toInt
        diffValue = (gainDb - value) / targetTime
        context.become(decay)
      } else {
        value += diffValue
        const.setConstant(value.toFloat)
      }

    case AdsrStopEvent =>
      println(s"[attack] ADSR stop event $value")
      time = 0
      targetTime = (adsrParams.release / AdsrConstants.rateSeconds).toInt
      diffValue = (AdsrConstants.zeroDb - value) / targetTime
      context.become(release)

    case msg: Any => {
      println(s"[attack] ADSR unhandled $msg")
      this.unhandled(msg)
    }

  }

  def decay: Receive = {
    case AdsrTimeEvent(_) =>
      cnt += 1
      if (cnt % logFreq == 0) println(s"[decay] ADSR time event $value")
      if (time > targetTime) {
        context.become(receive)
      } else {
        value += diffValue
        const.setConstant(value.toFloat)
      }

    case AdsrStartEvent =>
      println(s"[decay] ADSR start event $value")
      time = 0
      targetTime = (adsrParams.attack / AdsrConstants.rateSeconds).toInt
      diffValue = (gainDb - value) / targetTime
      context.become(attack)

    case AdsrStopEvent =>
      println(s"[decay] ADSR stop event $value")
      time = 0
      targetTime = (adsrParams.release / AdsrConstants.rateSeconds).toInt
      diffValue = (AdsrConstants.zeroDb - value) / targetTime
      context.become(release)

    case msg: Any => {
      println(s"[decay] ADSR unhandled $msg")
      this.unhandled(msg)
    }

  }

  def release: Receive = {
    case AdsrTimeEvent(_) =>
      cnt += 1
      if (cnt % logFreq == 0) println(s"[release] ADSR time event $value")
      if (time > targetTime) {
        context.become(receive)
      } else {
        value += diffValue
        const.setConstant(value.toFloat)
      }

    case AdsrStartEvent =>
      println(s"[release] ADSR start event $value")
      time = 0
      targetTime = (adsrParams.attack / AdsrConstants.rateSeconds).toInt
      diffValue = (gainDb - value) / targetTime
      context.become(attack)

    case msg: Any => {
      println(s"[release] ADSR unhandled $msg")
      this.unhandled(msg)
    }

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