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

    override def tinitusStart(): Unit = println("JVM NOT IMPLEMENTED")

    override def tinitusStop(): Unit = println("JVM NOT IMPLEMENTED")

    override def noiseWhiteStart(nineth: Nineth): Unit = println("JVM NOT IMPLEMENTED")

    override def noiseWhiteStop(): Unit = println("JVM NOT IMPLEMENTED")

    override def noisePinkStart(nineth: Nineth): Unit = println("JVM NOT IMPLEMENTED")

    override def noisePinkStop(): Unit = println("JVM NOT IMPLEMENTED")

    override def noiseBrownRedStart(nineth: Nineth): Unit = println("JVM NOT IMPLEMENTED")

    override def noiseBrownRedStop(): Unit = println("JVM NOT IMPLEMENTED")

    override def melodyStart(): Unit = println("JVM NOT IMPLEMENTED")

    override def adsrStart(nineth: Nineth): Unit = println("JVM NOT IMPLEMENTED " + nineth)

    override def adsrStop(): Unit = println("JVM NOT IMPLEMENTED")
  }

}

