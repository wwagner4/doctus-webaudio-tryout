package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class DoctuswebaudiotryoutDoctusTemplate(canvas: DoctusCanvas) extends DoctusTemplate {


  def draw(g: DoctusGraphics): Unit = {
    val w = canvas.width
    val h = canvas.height
    g.fill(DoctusColorYellow, 255)
    val nx = 3
    val ny = 4
    val dx = w.toDouble / nx
    val dy = h.toDouble / ny
    for (i <- 0 until nx; j <- 0 until ny) {
      g.rect(i * dx, j * dy, dx, dy)
    }
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

