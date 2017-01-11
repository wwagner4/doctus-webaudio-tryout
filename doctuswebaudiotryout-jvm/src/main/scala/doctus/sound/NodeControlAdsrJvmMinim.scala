package doctus.sound

import akka.actor.{Actor, ActorRef, Props}
import ddf.minim.UGen
import ddf.minim.ugens.{Constant, Line}

import scala.concurrent.duration._

case class NodeControlAdsrJvmMinim(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)(ctx: MinimContext) extends NodeControlEnvelope {

  private val adsrParams = AdsrParams(attack, decay, sustain, release, gain, trend)

  var adsrActor = Option.empty[ActorRef]

  /**
    * Starts the attack of the ADSR
    *
    * @param time Time in seconds
    */
  def start(time: Double): Unit = {
    val func = () => {
      println(f"ADSR started at $time%.2f")
      adsrActor.foreach(_ ! AdsrStartEvent)
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr start")
    ctx.tell(me)
  }

  def stop(time: Double): Unit = {
    val func = () => {
      println(f"ADSR stopped at $time%.2f")
      adsrActor.foreach(_ ! AdsrStopEvent)
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr stop")
    ctx.tell(me)
  }

  def connect(param: ControlParam): Unit = {
    param match {
      case p: UGenInputAware =>
        val _actor = ctx.actorSystem.actorOf(AdsrActor.props(p.uGenInput, adsrParams))
        adsrActor = Some(_actor)
        println(s"connecting ADSR (creating the actor)")

      case _ =>
        throw new IllegalArgumentException(
          s"cannot connect $this to $param. $param is not 'UGenAware'")
    }
  }
}


case object AdsrStartDecayEvent

case object AdsrStartSustainEvent

case object AdsrStopReleaseEvent

case object AdsrStartEvent

case object AdsrStopEvent

case class AdsrParams(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)

object AdsrActor {

  def props(ugenInput: UGen#UGenInput, adsrParams: AdsrParams): Props = Props(classOf[AdsrActor], ugenInput, adsrParams)

}

case class UgenValue(uGen: UGen) {

  def value: Float = {
    if (uGen.getLastValues.length > 1) throw new IllegalStateException("cannot calc value for more than one channel")
    uGen.getLastValues()(0)
  }

}

class AdsrActor(ugenInput: UGen#UGenInput, adsrParams: AdsrParams) extends Actor {

  val value = 0.0
  val minimConst: UGen = new Constant(value.toFloat)
  minimConst.patch(ugenInput)
  println(f"AdsrActor connected $value%.3f to $ugenInput")
  var ugenValue = UgenValue(minimConst)

  def receive: Receive = {

    case AdsrStartEvent =>
      val dt = adsrParams.attack
      val minimLine = new Line()
      minimLine.patch(ugenInput)
      minimLine.activate(dt.toFloat, ugenValue.value, adsrParams.gain.toFloat)
      ugenValue = UgenValue(minimLine)
      val fd = (dt * 1e6).toLong.micro
      context.system.scheduler.scheduleOnce(fd, self, AdsrStartDecayEvent)(context.dispatcher)
      context.become(attack)
      println(f"[receive] AdsrStartEvent Connected and activated Line for $dt%.5f seconds ($fd) on $ugenInput")

    case AdsrStopEvent =>
      val dt = adsrParams.release
      val minimLine = new Line()
      minimLine.patch(ugenInput)
      minimLine.activate(dt.toFloat, ugenValue.value, 0.0f)
      ugenValue = UgenValue(minimLine)
      context.system.scheduler.scheduleOnce((dt * 1e6).toLong.micro, self, AdsrStopReleaseEvent)(context.dispatcher)
      context.become(release)
      println(f"[receive] AdsrStopEvent")

    case msg: Any =>
      println(s"[receive] *ERROR* ADSR unhandled $msg")
      this.unhandled(msg)
  }

  def attack: Receive = {

    case AdsrStopEvent =>
      val dt = adsrParams.release
      val minimLine = new Line()
      minimLine.patch(ugenInput)
      minimLine.activate(dt.toFloat, ugenValue.value, 0f)
      ugenValue = UgenValue(minimLine)
      context.system.scheduler.scheduleOnce((dt * 1e6).toLong.micro, self, AdsrStopReleaseEvent)(context.dispatcher)
      context.become(release)
      println(f"[attack] AdsrStopEvent")

    case AdsrStartDecayEvent =>
      val dt = adsrParams.decay
      val minimLine = new Line()
      minimLine.patch(ugenInput)
      val _v = ugenValue.value
      minimLine.activate(dt.toFloat, adsrParams.gain.toFloat, (adsrParams.gain.toFloat * adsrParams.sustain).toFloat)
      ugenValue = UgenValue(minimLine)
      context.system.scheduler.scheduleOnce((dt * 1e6).toLong.micro, self, AdsrStartSustainEvent)(context.dispatcher)
      context.become(decay)
      println(f"[attack] AdsrStartDecayEvent v:${_v}%.3f")

    case msg: Any =>
      println(s"[attack] *ERROR* ADSR unhandled $msg")
      this.unhandled(msg)
  }

  def decay: Receive = {

    case AdsrStopEvent =>
      val dt = adsrParams.release
      val minimLine = new Line()
      minimLine.patch(ugenInput)
      minimLine.activate(dt.toFloat, ugenValue.value, 0f)
      ugenValue = UgenValue(minimLine)
      context.system.scheduler.scheduleOnce((dt * 1e6).toLong.micro, self, AdsrStopReleaseEvent)(context.dispatcher)
      context.become(release)
      println(f"[decay] AdsrStopEvent")

    case AdsrStartSustainEvent =>
      val minimConst = new Constant((adsrParams.gain * adsrParams.sustain).toFloat)
      ugenValue = UgenValue(minimConst)
      minimConst.patch(ugenInput)
      context.become(receive)
      println(f"[decay] AdsrStartSustainEvent")

    case msg: Any =>
      println(s"[decay] *ERROR* ADSR unhandled $msg")
      this.unhandled(msg)
  }

  def release: Receive = {

    case AdsrStopReleaseEvent =>
      val minimConst = new Constant(0.0f)
      ugenValue = UgenValue(minimConst)
      minimConst.patch(ugenInput)
      context.become(receive)
      println(f"[release] AdsrStopReleaseEvent")

    case msg: Any =>
      println(s"[release] *ERROR* ADSR unhandled $msg")
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