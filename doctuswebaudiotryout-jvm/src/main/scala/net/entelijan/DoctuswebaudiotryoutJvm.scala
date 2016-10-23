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

    var ctxOpt = Option.empty[DoctusSoundAudioContextJvmMinim]

    override def start(stage: Stage) {

      val width = 500
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

      val ctx = new DoctusSoundAudioContextJvmMinim {}
      ctxOpt = Some(ctx)

      // Common to all platforms
      val templ = DoctuswebaudiotryoutTemplate(canvas, ctx)
      DoctusTemplateController(templ, sched, canvas)

      stage.setScene(scene)

      stage.show()
      def handler[T <: Event](h: (T => Unit)): EventHandler[T] =
        new EventHandler[T] {
          override def handle(event: T): Unit = h(event)
        }

      // Find a better solution to exit
      stage.setOnCloseRequest(handler { e =>
        ctxOpt.foreach(ctx => ctx.terminate)
        System.exit(0)
      })

    }

  }

}

