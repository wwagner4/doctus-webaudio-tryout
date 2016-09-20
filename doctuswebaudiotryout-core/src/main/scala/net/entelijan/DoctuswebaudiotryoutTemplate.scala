// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.core._
import doctus.core.color._
import doctus.core.template.DoctusTemplate
import doctus.core.text.DoctusFontNamed
import doctus.core.util.DoctusPoint
import doctus.sound._

sealed trait Nineth

case object N_00 extends Nineth
case object N_01 extends Nineth
case object N_02 extends Nineth
case object N_10 extends Nineth
case object N_11 extends Nineth
case object N_12 extends Nineth
case object N_20 extends Nineth
case object N_21 extends Nineth
case object N_22 extends Nineth

trait SoundExperiment {

  def title: String

  def start(nineth: Nineth)

  def stop()

}

case class Tile(i: Int, j: Int, dx: Double, dy: Double)

case class DoctuswebaudiotryoutTemplate(canvas: DoctusCanvas, soundContext: DoctusSoundAudioContext) extends DoctusTemplate {

  val experimentManager = SoundExperimentManager(soundContext)

  val nx = 4
  val ny = 4

  val tilesCnt = nx * ny

  override def frameRate: Option[Int] = None


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
      val tile = Tile(i, j, dx, dy)
      g.rect(i * dx, j * dy, dx, dy)
      writeText(g, tile)
      cnt += 1
    }
  }

  def writeText(g: DoctusGraphics, tile: Tile): Unit = {
    val exp = experimentManager.experiment(tile)
    writeText(g, tile, exp.title)
  }

  def writeText(g: DoctusGraphics, tile: Tile, text: String): Unit = {
    g.textSize(15)
    g.textFont(DoctusFontNamed("Jura"))
    g.fill(DoctusColorBlack, 255)
    g.text(text, tile.i * tile.dx + 5, tile.j * tile.dy + 20, 0)
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

  def pointablePressed(pos: DoctusPoint): Unit = {
    val t = tile(pos)
    val exp = experimentManager.experiment(t)
    exp.start(nineth(pos, t))
  }

  def pointableReleased(pos: DoctusPoint): Unit = {
    val t = tile(pos)
    val exp = experimentManager.experiment(t)
    exp.stop()
  }

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

  def tile(pos: DoctusPoint): Tile = {
    val w = canvas.width
    val h = canvas.height
    val dx = w.toDouble / nx
    val dy = h.toDouble / ny
    val i = math.floor(pos.x / dx).toInt
    val j = math.floor(pos.y / dy).toInt
    Tile(i, j, dx, dy)
  }

  def nineth(pos: DoctusPoint, tile: Tile): Nineth = {

    def rowCol: (Int, Int) = {
      val o = DoctusPoint(tile.dx * tile.i, tile.dy * tile.j)
      val v = pos - o
      val x1 = tile.dx / 3
      val x2 = 2 * tile.dx / 3
      val i = if (v.x < x1) 0 else if (v.x < x2) 1 else 2
      val y1 = tile.dy / 3
      val y2 = 2 * tile.dy / 3
      val j = if (v.y < y1) 0 else if (v.y < y2) 1 else 2
      (i, j)
    }

    rowCol match {
      case (0, 0) => N_00
      case (0, 1) => N_01
      case (0, 2) => N_02
      case (1, 0) => N_10
      case (1, 1) => N_11
      case (1, 2) => N_12
      case (2, 0) => N_20
      case (2, 1) => N_21
      case (2, 2) => N_22
    }
  }
}

