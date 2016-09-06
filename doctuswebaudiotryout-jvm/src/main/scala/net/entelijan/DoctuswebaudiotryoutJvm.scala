// Copyright (C) 2016 wolfgang wagner http://entelijan.net

package net.entelijan

import javafx.application.Application
import javafx.event.{Event, EventHandler}
import javafx.scene._
import javafx.scene.canvas.Canvas
import javafx.scene.paint._
import javafx.stage.Stage

import doctus.core.template._
import doctus.jvm._
import doctus.sound._


object DoctuswebaudiotryoutJvm extends App {

  Application.launch(classOf[MyApp], args: _*)

  class MyApp extends Application {

    override def start(stage: Stage) {

      val width = 700
      val height = 500

      val canvasFx = new Canvas(width, height)

      val sched = DoctusSchedulerJvm
      val canvas = DoctusTemplateCanvasFx(canvasFx)


      val grp = new Group()
      grp.getChildren.add(canvasFx)

      val bgCol = Color.WHITE
      val scene = new Scene(grp, width, height, bgCol)
      canvasFx.widthProperty().bind(scene.widthProperty())
      canvasFx.heightProperty().bind(scene.heightProperty())

      // Common to all platforms
      val templ = DoctuswebaudiotryoutTemplate(canvas, DoctusSoundJvm)
      DoctusTemplateController(templ, sched, canvas)

      stage.setScene(scene)

      stage.show()
      def handler[T <: Event](h: (T => Unit)): EventHandler[T] =
        new EventHandler[T] {
          override def handle(event: T): Unit = h(event)
        }

      // Find a better solution to exit
      stage.setOnCloseRequest(handler(e => System.exit(0)))



    }
  }

  case object DoctusSoundJvm extends DoctusSound {

    override def tinnitusStart(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def tinnitusStop(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def noiseWhiteStart(nineth: Nineth): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def noiseWhiteStop(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def noisePinkStart(nineth: Nineth): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def noisePinkStop(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def noiseBrownRedStart(nineth: Nineth): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def noiseBrownRedStop(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def melodyStart(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    override def adsrStart(nineth: Nineth): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED " + nineth)

    override def adsrStop(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    def metalStart(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")
  
    def metalStop(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")
  
    def filterStart(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

    def filterStop(): Unit = throw new IllegalStateException("JVM NOT IMPLEMENTED")

  }

}

