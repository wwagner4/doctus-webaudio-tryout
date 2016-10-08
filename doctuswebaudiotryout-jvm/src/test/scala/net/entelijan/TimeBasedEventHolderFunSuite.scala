package net.entelijan

import doctus.sound.{TimeBasedEvent, TimeBasedEventHolder}
import org.scalatest.FunSuite

/**
  * Tests the behaviour of TimeBasedEventHolder
  */
class TimeBasedEventHolderFunSuite extends FunSuite {

  case class TestEvent(executionTime: Double, data: String) extends TimeBasedEvent[String] {}

  test("Empty holder returns empty list") {
    val eh = TimeBasedEventHolder.empty[String]
    val r = eh.detectEvents(0.0)

    assert(r.events.isEmpty === true)
  }

  test("Adding one event to an empty holder returns list with this element when time is equal to event time") {
    val eh = TimeBasedEventHolder.empty[String]
    eh.addEvent(TestEvent(1, "A"))
    val r = eh.detectEvents(1)
    assert(r.events(0).data === "A")
  }

  test("Adding one event to an empty holder returns list with this element when time is greater than event time") {
    val eh = TimeBasedEventHolder.empty[String]
    eh.addEvent(TestEvent(1, "A"))
    val r = eh.detectEvents(1)
    assert(r.events(0).data === "A")
  }

  test("Holder with multiple events. Detect none of the events") {
    val eh = TimeBasedEventHolder.empty[String]
    eh.addEvent(TestEvent(10.0, "A"))
    eh.addEvent(TestEvent(20.0, "B"))
    eh.addEvent(TestEvent(30.0, "C"))
    val r = eh.detectEvents(9.0)
    assert(r.events.isEmpty === true)
  }

  test("Holder with multiple events. Detect some of the events") {
    val eh = TimeBasedEventHolder.empty[String]
    eh.addEvent(TestEvent(10.0, "A"))
    eh.addEvent(TestEvent(20.0, "B"))
    eh.addEvent(TestEvent(30.0, "C"))
    val r = eh.detectEvents(20.0)
    assert(r.events.size === 2)
    assert(r.events(0).data === "A")
    assert(r.events(1).data === "B")
  }

  test("Holder with multiple events added in reverse order. Detect some of the events") {
    val eh = TimeBasedEventHolder.empty[String]
    eh.addEvent(TestEvent(30.0, "C"))
    eh.addEvent(TestEvent(20.0, "B"))
    eh.addEvent(TestEvent(10.0, "A"))
    val r = eh.detectEvents(20.0)
    assert(r.events.size === 2)
    assert(r.events(0).data === "A")
    assert(r.events(1).data === "B")

    val r1 = r.nextHolder.detectEvents(30.0)
    assert(r1.events.size === 1)
    assert(r1.events(0).data === "C")

  }

  test("Holder with multiple events. Detect all of the events") {
    val eh = TimeBasedEventHolder.empty[String]
    eh.addEvent(TestEvent(10.0, "A"))
    eh.addEvent(TestEvent(20.0, "B"))
    eh.addEvent(TestEvent(30.0, "C"))
    val r = eh.detectEvents(50.0)
    assert(r.events.size === 3)
    assert(r.events(0).data === "A")
    assert(r.events(1).data === "B")
    assert(r.events(2).data === "C")
  }

  test("Holder with multiple events. Detect all of the events. The resulting handler is empty") {
    val eh = TimeBasedEventHolder.empty[String]
    eh.addEvent(TestEvent(10.0, "A"))
    eh.addEvent(TestEvent(20.0, "B"))
    eh.addEvent(TestEvent(30.0, "C"))
    val r0 = eh.detectEvents(50.0)

    val reh = r0.nextHolder

    val r1 = reh.detectEvents(50.0)
    assert(r1.events.isEmpty === true)
  }

}
