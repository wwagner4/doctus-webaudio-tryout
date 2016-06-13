package net.entelijan

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLCanvasElement
import doctus.scalajs._
import doctus.core.template._
import doctus.sound.DoctusSoundJs

@JSExport("DoctuswebaudiotryoutScalaJs")
object DoctuswebaudiotryoutScalaJs {

  @JSExport
  def main() {

    val canvasElem: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    val canvas = DoctusTemplateCanvasScalajs(canvasElem)
    val sched = DoctusSchedulerScalajs
    val sound = new DoctusSoundJs

    // Common to all platforms
    val templ = net.entelijan.DoctuswebaudiotryoutDoctusTemplate(canvas, sound)
    DoctusTemplateController(templ, sched, canvas)


  }

}