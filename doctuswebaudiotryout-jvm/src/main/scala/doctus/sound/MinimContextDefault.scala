package doctus.sound

import java.io.{File, FileInputStream, InputStream}

import akka.actor.{Actor, ActorSystem, Props}
import ddf.minim.Minim
import ddf.minim.javasound.JSMinim

/**
  * Created by wolfi on 14.10.16.
  */
case class MinimContextDefault() extends MinimContext  {

  import scala.concurrent.duration._

  private val startTime = System.nanoTime()

  private val _minim = {
    val fileLoader = FileLoaderUserHome()
    val serviceProvider = new JSMinim(fileLoader)
    new Minim(serviceProvider)
  }

  val sys = ActorSystem.create()

  val _musicActor = sys.actorOf(MusicActor.props)

  val funcCreateTimeEvent = () => {
    val timeEvent = TimeEvent(currentTime)
    _musicActor ! timeEvent
  }

  sys.scheduler.schedule(0.second, 7812.micro)(funcCreateTimeEvent())(sys.dispatcher)

  def tell(message: Any): Unit = _musicActor ! message

  def minim: Minim = _minim

  def terminate: Unit = {
    _minim.stop()
    sys.terminate()
  }

  def currentTime: Double = (System.nanoTime() - startTime) / 1.0e9

  def actorSystem: ActorSystem = sys

}

case class FileLoaderUserHome() {

  def sketchPath(fileName: String): String = {
    val file = getCreateFile(fileName)
    file.getAbsolutePath
  }

  def createInput(fileName: String): InputStream = {
    new FileInputStream(fileName)
  }

  private def getCreateFile(fileName: String): File = {
    val home = new File(System.getProperty("user.home"))
    val outDir = new File(home, "minim_out")
    outDir.mkdirs()
    new File(outDir, fileName)
  }

}

case class TimeEvent(currentTime: Double)

trait TimeBasedEvent[T] {

  def executionTime: Double

  def data: T

}

// Not serializable. Though the best solution (eventually)
// as long as we stay within one VM
case class MusicEvent(executionTime: Double, data: () => Unit) extends TimeBasedEvent[() => Unit]

object MusicActor {

  def props: Props = Props[MusicActor]

}

class MusicActor extends Actor {

  var eventHolder = TimeBasedEventHolder.empty[() => Unit]

  def receive: Receive = {
    case musicEvent: MusicEvent =>
      eventHolder.addEvent(musicEvent)
    case TimeEvent(time) =>
      val r = eventHolder.detectEvents(time)
      r.events.foreach(evt => evt.data())
      eventHolder = r.nextHolder
    case message: Any =>
      unhandled(message)
  }
}

case class TimeBasedEventHolderResult[T](nextHolder: TimeBasedEventHolder[T], events: List[TimeBasedEvent[T]])

object TimeBasedEventHolder {

  def empty[T]: TimeBasedEventHolder[T] = TimeBasedEventHolderImpl[T](List.empty[TimeBasedEvent[T]])

  case class TimeBasedEventHolderImpl[T](initialEvents: List[TimeBasedEvent[T]]) extends TimeBasedEventHolder[T] {

    var events = initialEvents

    // TODO. Performance tuning: Keep the events ordered by execution time
    def detectEvents(time: Double): TimeBasedEventHolderResult[T] = {
      val resultEvents = events.filter { e => e.executionTime <= time }.sortBy(e => e.executionTime)
      val restEvents = events.diff(resultEvents)
      TimeBasedEventHolderResult(TimeBasedEventHolderImpl(restEvents), resultEvents)
    }

    def addEvent(event: TimeBasedEvent[T]): Unit = {
      events = event :: events
    }

  }

}

trait TimeBasedEventHolder[T] {

  /**
    * Detects all events with 'executionTime' before or equal to 'time'
    * These events will be returned and removed from the holder
    *
    * @param time defines which events have to be executed
    * @return The events and a new instance of the holder
    */
  def detectEvents(time: Double): TimeBasedEventHolderResult[T]

  /**
    * @param event that will be added to the holder
    */
  def addEvent(event: TimeBasedEvent[T]): Unit

}


