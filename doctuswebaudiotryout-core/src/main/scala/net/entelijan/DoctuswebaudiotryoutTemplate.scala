package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class WordPos(word: String, x: Double, y: Double, size: Double)

/**
 * In here comes the complete logic for your project
 */
case class DoctuswebaudiotryoutDoctusTemplate(canvas: DoctusCanvas) extends DoctusTemplate {

  val ran = new java.util.Random

  val mx = canvas.width / 2.0 - 80
  val my = canvas.height / 2.0
  val wordList = List("here", "comes", "your", "creativity")
  var words = wordList.map(word => WordPos(word, mx, my, 50))

  def draw(g: DoctusGraphics): Unit = {
    val w = canvas.width
    val h = canvas.height

    def drawBackground(g: DoctusGraphics): Unit = {
      g.fill(DoctusColorBlack, 30)
      g.rect(DoctusPoint(0, 0), w, h)
    }

    def drawWord(wp: WordPos): Unit = {
      g.textSize(wp.size)
      g.text(wp.word, DoctusPoint(wp.x, wp.y), 0)
    }

    def next(l: WordPos): WordPos = {
      val d1 = ran.nextInt(23) - 11
      val d2 = ran.nextInt(17) - 8
      val d3 = ran.nextInt(7) - 3
      WordPos(l.word, l.x + d1, l.y + d2, l.size + d3)
    }

    drawBackground(g)
    g.fill(DoctusColorWhite, 255)
    words = words.map(next)
    words.foreach(drawWord)
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

