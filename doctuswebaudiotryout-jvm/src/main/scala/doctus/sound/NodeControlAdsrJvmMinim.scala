package doctus.sound

import akka.actor.{ Actor, ActorRef, Props }
import ddf.minim.ugens.Constant
import scala.concurrent.duration._

object AdsrConstants {

  val rate = 7813.micro
  val rateSeconds: Double = rate.toMicros * 1.0e-6
  val zeroDb = 0.0

}

case class NodeControlAdsrJvmMinim(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)(ctx: MinimContext) extends NodeControlEnvelope {

  private val minimConst = new Constant(AdsrConstants.zeroDb.toFloat)

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

  def props(minimConst: Constant, adsrParams: AdsrParams): Props = Props(classOf[AdsrActor], minimConst, adsrParams)

}

case class AdsrParams(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)

case class AdsrMsg(time: Double)

class AdsrActor(minimConst: Constant, adsrParams: AdsrParams) extends Actor {
  
  def constValue(v: Double) {
    println(f"value >- $v%.2f")
    minimConst.setConstant(v.toFloat)
  }

  var cnt = 0

  var value = AdsrConstants.zeroDb
  var diffValue = 0.0

  var time: Integer = 0
  var targetTime: Integer = 0

  val gainDb = adsrParams.gain
  val sustainDb = adsrParams.gain * adsrParams.sustain

  def receive: Receive = {
    case AdsrTimeEvent(sysTime) =>
      cnt += 1
      //println(f"[receive] ADSR time event $value%.3f t:$time st:$sysTime%.3f")

    case AdsrStartEvent =>
      time = 0
      targetTime = (adsrParams.attack / AdsrConstants.rateSeconds).toInt
      diffValue = if (targetTime > 0) (gainDb - value) / targetTime else 0.0
      println(f"[receive] ADSR start event $value%.3f rateSeconds:${AdsrConstants.rateSeconds}%.3f attack:${adsrParams.attack}%.3f targetTime:$targetTime")
      context.become(attack)

    case AdsrStopEvent =>
      println(f"[receive] ADSR stop event $value%.3f")
      time = 0
      targetTime = (adsrParams.release / AdsrConstants.rateSeconds).toInt
      diffValue = if (targetTime > 0) (AdsrConstants.zeroDb - value) / targetTime else 0.0
      context.become(release)

    case msg: Any =>
      println(s"[receive] ADSR unhandled $msg")
      this.unhandled(msg)
  }

  def attack: Receive = {
    case AdsrTimeEvent(sysTime) =>
      time += 1
      cnt += 1
      //println(f"[attack] ADSR time event $value%.3f t:$time st:$sysTime%.3f")
      if (time > targetTime) {
        time = 0
        if (adsrParams.decay <= 0.0) {
          if (adsrParams.release <= 0.0) {
            value = AdsrConstants.zeroDb
            constValue(value)
            context.become(receive)
          } else {
            context.become(receive)
          }
        } else {
          value = gainDb
          constValue(value)
          targetTime = (adsrParams.decay / AdsrConstants.rateSeconds).toInt
          diffValue = if (targetTime > 0) (sustainDb - value) / targetTime else 0.0
          println(f"become decay sdb:$sustainDb%.3f sdb:$value%.3f  tt:$targetTime dv:$diffValue%.3f v:$value%.3f")
          context.become(decay)
        }
      } else {
        value += diffValue
        constValue(value.toFloat)
      }

    case AdsrStopEvent =>
      println(f"[attack] ADSR stop event $value%.3f")
      time = 0
      targetTime = (adsrParams.release / AdsrConstants.rateSeconds).toInt
      diffValue = if (targetTime > 0) (AdsrConstants.zeroDb - value) / targetTime else 0.0
      context.become(release)

    case msg: Any =>
      println(f"[attack] ADSR unhandled $msg")
      this.unhandled(msg)

  }

  def decay: Receive = {
    case AdsrTimeEvent(sysTime) =>
      time += 1
      cnt += 1
      //println(f"[decay] ADSR time event $value%.3f t:$time st:$sysTime%.3f")
      if (time > targetTime) {
        println(f"[decay] become 'receive'")
        value = sustainDb
        constValue(value.toFloat)
        context.become(receive)
      } else {
        value += diffValue
        constValue(value)
      }

    case AdsrStartEvent =>
      println(f"[decay] ADSR start event $value%.3f")
      time = 0
      targetTime = (adsrParams.attack / AdsrConstants.rateSeconds).toInt
      diffValue = if (targetTime > 0) (gainDb - value) / targetTime else 0.0
      context.become(attack)

    case AdsrStopEvent =>
      println(f"[decay] ADSR stop event $value%.3f")
      time = 0
      targetTime = (adsrParams.release / AdsrConstants.rateSeconds).toInt
      diffValue = if (targetTime > 0) (AdsrConstants.zeroDb - value) / targetTime else 0.0
      context.become(release)

    case msg: Any =>
      println(f"[decay] ADSR unhandled $msg")
      this.unhandled(msg)

  }

  def release: Receive = {
    case AdsrTimeEvent(sysTime) =>
      time += 1
      cnt += 1
      //println(f"[release] ADSR time event $value%.3f t:$time st:$sysTime%.3f")
      if (time > targetTime) {
        println(f"[release] become 'receive'")
        value = AdsrConstants.zeroDb
        constValue(value)
        context.become(receive)
      } else {
        value += diffValue
        constValue(value)
      }

    case AdsrStartEvent =>
      println(f"[release] ADSR start event $value%.3f")
      time = 0
      targetTime = (adsrParams.attack / AdsrConstants.rateSeconds).toInt
      diffValue = if (targetTime > 0) (gainDb - value) / targetTime else 0.0
      context.become(attack)

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