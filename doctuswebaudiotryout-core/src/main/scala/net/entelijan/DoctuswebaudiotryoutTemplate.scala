package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._
import doctus.sound.DoctusSound

case class DoctuswebaudiotryoutDoctusTemplate(canvas: DoctusCanvas, sound: DoctusSound) extends DoctusTemplate {

  val nx = 4
  val ny = 4

  val tilesCnt = nx * ny

  override def frameRate = None

  def draw(g: DoctusGraphics): Unit = {
    val w = canvas.width
    val h = canvas.height
    val dx = w.toDouble / nx
    val dy = h.toDouble / ny
    val colorsStream = colors
    var cnt = 0
    g.noStroke()
    for (i <- 0 until nx; j <- 0 until ny) {
      g.fill(colorsStream(cnt), 255)
      g.rect(i * dx, j * dy, dx, dy)
      cnt += 1
    }
  }

  def colors: Stream[DoctusColor] = {
    def nextColor(c: DoctusColor): DoctusColor = {
      val (r1, g1, b1) = c.rgb
      val (h1, s1, v1) = DoctusColorUtil.rgb2hsv(r1, g1, b1)
      val h2 = (h1 + (360.toDouble / tilesCnt).toInt) % 360
      val (r2, g2, b2) = DoctusColorUtil.hsv2rgb(h2, s1, v1)
      DoctusColorRgb(r2, g2, b2)
    }
    val (sr, sg, sb) = DoctusColorUtil.hsv2rgb(55, 90, 80)
    val start: DoctusColor = DoctusColorRgb(sr, sg, sb)
    Stream.iterate(start)(nextColor)
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = {
    val w = canvas.width
    val h = canvas.height
    val dx = w.toDouble / nx
    val dy = h.toDouble / ny
    val i = math.floor(pos.x / dx).toInt
    val j = math.floor(pos.y / dy).toInt
    (i, j) match {
      case (0, 0) => sound.oscilOn
      case (1, 0) => sound.oscilOff
      case _ => // Nothing to do
    }
  }

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

