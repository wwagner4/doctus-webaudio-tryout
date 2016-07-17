// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import doctus.core._
import doctus.core.color._
import doctus.core.template.DoctusTemplate
import doctus.core.text.DoctusFontNamed
import doctus.core.util.DoctusPoint
import doctus.sound._

case class DoctuswebaudiotryoutTemplate(canvas: DoctusCanvas, sound: DoctusSound) extends DoctusTemplate {

  case class Tile(i: Int, j: Int, dx: Double, dy: Double)

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
    tile match {
      case Tile(0, 0, _, _) => writeText(g, tile, "tinitus")
      case Tile(0, 1, _, _) => writeText(g, tile, "melody")
      case Tile(0, 2, _, _) => writeText(g, tile, "white noise")
      case Tile(0, 3, _, _) => writeText(g, tile, "pink noise")
      case Tile(1, 0, _, _) => writeText(g, tile, "brown/red noise")
      case Tile(1, 1, _, _) => writeText(g, tile, "ADSR")
      case Tile(1, 2, _, _) => writeText(g, tile, "metal")
      case Tile(1, 3, _, _) => writeText(g, tile, "filter")
      case _ => // nothing to do here
    }
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
    tile(pos) match {
      case Tile(0, 0, _, _) => sound.tinitusStart()
      case Tile(0, 1, _, _) => sound.melodyStart()
      case tile @ Tile(0, 2, _, _) => sound.noiseWhiteStart(nineth(pos, tile))
      case tile @ Tile(0, 3, _, _) => sound.noisePinkStart(nineth(pos, tile))
      case tile @ Tile(1, 0, _, _) => sound.noiseBrownRedStart(nineth(pos, tile))
      case tile @ Tile(1, 1, _, _) => sound.adsrStart(nineth(pos, tile))
      case tile @ Tile(1, 2, _, _) => sound.metalStart()
      case tile @ Tile(1, 3, _, _) => sound.filterStart()
      case _ => // Nothing to do
    }
  }

  def pointableReleased(pos: DoctusPoint): Unit = {
    tile(pos) match {
      case Tile(0, 0, _, _) => sound.tinitusStop()
      case Tile(0, 2, _, _) => sound.noiseWhiteStop()
      case Tile(0, 3, _, _) => sound.noisePinkStop()
      case Tile(1, 0, _, _) => sound.noiseBrownRedStop()
      case Tile(1, 1, _, _) => sound.adsrStop()
      case Tile(1, 2, _, _) => sound.metalStop()
      case Tile(1, 3, _, _) => sound.filterStop()
      case _ => // Nothing to do
    }
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

