package doctus.sound

import ddf.minim.ugens.ADSR
import ddf.minim.ugens.Constant

case class NodeControlAdsrJvmMinim(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: Trend)
                                  (ctx: MinimContext) extends NodeControlEnvelope {

  private val minimConst = new Constant(0.0f)

  def start(time: Double): Unit = {
    val func = () => {
      println(f"ADSR started at $time%.2f")
    }
    val me = MusicEvent(time, func)
    println(s"Telling MUSICEVENT $me adsr start")
    ctx.tell(me)
  }

  def stop(time: Double): Unit = {
    val func = () => {
      println(f"ADSR stopped at $time%.2f")
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

