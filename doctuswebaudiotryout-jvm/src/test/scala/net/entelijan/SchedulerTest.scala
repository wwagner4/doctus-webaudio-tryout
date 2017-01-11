package net.entelijan

import akka.actor.{Actor, ActorSystem, Props}


/**
  * Test for akka schedulers with short intervals
  * > 125 per second
  */
object SchedulerTest extends App {

  import scala.concurrent.duration._

  println("stared")
  val sys = ActorSystem.create()
  println("created system")

  val rates = List(10, 50, 100, 150, 200, 400, 800, 1500, 3000)

  rates.foreach(rate => test(rate))


  sys.terminate()
  pause(1.second)
  println("terminated")


  def pause(duration: FiniteDuration): Unit = {
    Thread.sleep(duration.toMillis)
  }

  def test(rate: Int): Unit = {
    val actor = sys.actorOf(Props[SchedulerTestActor])

    val intervalNano = (1.0e9 / rate).toLong

    val interval = intervalNano.nano

    val intervalMili = 1e-6 * intervalNano

    //println("%10d %10d %10s" format(rate, intervalNano, interval))

    val c = sys.scheduler.schedule(0.second, interval, actor, T(rate, intervalMili))(sys.dispatcher)

    pause(1800.milli)

    c.cancel()
    //println("cancelled")
    actor ! E

  }


}

case class T(rate: Long, interv: Double)
case object E

class SchedulerTestActor extends Actor {


  var errList = List.empty[Double]
  var diffList = List.empty[Double]
  val start = System.nanoTime()
  var lastTime = Option.empty[Long]
  var inter = 0.0
  var rate = 0L

  def receive: Receive = {
    case T(rate, interv) =>
      val now = System.nanoTime()
      val t = now - start
      inter = interv
      this.rate = rate
      lastTime.foreach{lt =>
        val diff = (t - lt) * 1e-6
        val err = diff - interv
        //println("%10d %10.4f %10.4f %10.4f" format(rate, diff, interv, err))
        errList ::= err
        diffList ::= diff
      }
      lastTime = Some(t)

    case E =>
//      println("ended")
      val diffMean = diffList.sum / diffList.size
      val errMean = errList.sum / errList.size
      val errRel = errMean / inter


      println("%10d %10d %10.4f %10.4f %10.4f %10.4f" format(errList.size, rate, inter, diffMean, errMean, errRel))

  }

}