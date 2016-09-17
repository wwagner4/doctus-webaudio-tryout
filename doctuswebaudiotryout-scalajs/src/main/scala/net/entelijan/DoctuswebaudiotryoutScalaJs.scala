// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLCanvasElement
import doctus.scalajs._
import doctus.core.template._
import doctus.sound._
import org.scalajs.dom.AudioContext

@JSExport("DoctuswebaudiotryoutScalaJs")
object DoctuswebaudiotryoutScalaJs {

  @JSExport
  def main() {

    val canvasElem: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    val canvas = DoctusTemplateCanvasScalajs(canvasElem)
    val sched = DoctusSchedulerScalajs
    val ctx = DoctusSoundAudioContextScalajs(new AudioContext())

    // Common to all platforms
    val templ = net.entelijan.DoctuswebaudiotryoutTemplate(canvas, ctx)
    DoctusTemplateController(templ, sched, canvas)

  }

}
