package net.entelijan

import doctus.sound.DoctusSoundAudioContext

/**
  * Created by wwagner4 on 18/09/16.
  */
case class SoundExperimentManager(soundContext: DoctusSoundAudioContext) {

  lazy val tinnitus = Tinnitus(soundContext)

  def experiment: Tile => SoundExperiment = {
    case Tile(0, 0, _, _) => tinnitus
    case _ => new SoundExperiment {
      // Empty sound experiment

      def title: String = ""

      def start(nienth: Nineth): Unit = ()

      def stop(): Unit = ()

    }
  }



}
