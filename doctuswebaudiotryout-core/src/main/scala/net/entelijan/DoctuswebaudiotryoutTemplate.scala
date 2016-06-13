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
    def nextColor(c: DoctusColor): DoctusColor = {
      val (r1, g1, b1) = c.rgb
      val (h, s, v) = DoctusColorUtil.rgb2hsv(r1, g1, b1)
      val hue = (h + 30) % 360
      val (r, g, b) = DoctusColorUtil.hsv2rgb(hue, s, v)
      DoctusColorRgb(r, g, b)
    }
    val (sr, sg, sb) = DoctusColorUtil.hsv2rgb(122, 100, 50)
    val start: DoctusColor = DoctusColorRgb(sr, sg, sb)
    Stream.iterate(start)(c => nextColor(c))
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

