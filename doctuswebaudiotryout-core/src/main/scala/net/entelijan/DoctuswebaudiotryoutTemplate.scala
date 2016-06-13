package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class DoctuswebaudiotryoutDoctusTemplate(canvas: DoctusCanvas) extends DoctusTemplate {


  override def frameRate = None

  def draw(g: DoctusGraphics): Unit = {
    val w = canvas.width
    val h = canvas.height
    val nx = 4
    val ny = 4
    val dx = w.toDouble / nx
    val dy = h.toDouble / ny
    val colorsStream = colors
    var colorIndex = 0
    g.noStroke()
    for (i <- 0 until nx; j <- 0 until ny) {
      g.fill(colorsStream(colorIndex), 255)
      g.rect(i * dx, j * dy, dx, dy)
      colorIndex += 1
    }
  }


  def colors: Stream[DoctusColor] = {
    def ranColor: DoctusColor = {
      val hue = math.random * 365
      val (r, g, b) = DoctusColorUtil.hsv2rgb(hue.toInt, 50, 100)
      DoctusColorRgb(r, g, b)
    }
    Stream.iterate(ranColor)(_ => ranColor)
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

