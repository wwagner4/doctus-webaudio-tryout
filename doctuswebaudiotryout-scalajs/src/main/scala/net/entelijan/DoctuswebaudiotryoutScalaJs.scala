// Copyright (C) 2016 wolfgang wagner http://entelijan.net
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0

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
    val templ = net.entelijan.DoctuswebaudiotryoutTemplate(canvas, sound)
    DoctusTemplateController(templ, sched, canvas)


  }

}
