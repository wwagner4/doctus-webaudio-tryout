package doctus.sound

import akka.actor.AbstractSchedulerBase

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext
import akka.actor.Cancellable
import java.util.concurrent.{Executors, ScheduledExecutorService, ThreadFactory, TimeUnit}

import akka.event.LoggingAdapter
import com.typesafe.config.Config

class DoctusSoundScheduler(cfg: Config, la: LoggingAdapter, tf: ThreadFactory) extends AbstractSchedulerBase {

  def maxFrequency(): Double = 1000

  def schedule(initialDelay: FiniteDuration, interval: FiniteDuration, runnable: Runnable)(implicit executor: ExecutionContext): Cancellable = {
    var cancelled = false;
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    val future = scheduler.scheduleAtFixedRate(runnable, initialDelay.toMicros, interval.toMicros, TimeUnit.MICROSECONDS)
    new Cancellable {

      def cancel(): Boolean = {
        cancelled = future.cancel(true)
        cancelled
      }

      def isCancelled: Boolean = cancelled

    }
  }

  def scheduleOnce(delay: FiniteDuration, runnable: Runnable)(implicit executor: ExecutionContext): Cancellable = {
    var cancelled = false;
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    val future = scheduler.schedule(runnable, delay.toMicros, TimeUnit.MICROSECONDS)
    new Cancellable {

      def cancel(): Boolean = {
        cancelled = future.cancel(true)
        cancelled
      }

      def isCancelled: Boolean = cancelled

    }
  }

}