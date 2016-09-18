package net.entelijan

import doctus.sound.DoctusSoundAudioContext
import net.entelijan.experiments._

/**
  * Created by wwagner4 on 18/09/16.
  */
case class SoundExperimentManager(soundContext: DoctusSoundAudioContext) {

  lazy val tinnitus = Tinnitus(soundContext)
  lazy val melody = Melody(soundContext)
  lazy val noise = Noise(soundContext)
  lazy val adsr = AdsrTryout(soundContext)
  lazy val metal  = MetalTryout(soundContext)
  lazy val filter = FilterTryout(soundContext)

  def experiment: Tile => SoundExperiment = {
    case Tile(0, 0, _, _) => tinnitus
    case Tile(0, 1, _, _) => melody
    case Tile(0, 2, _, _) => noise
    case Tile(0, 3, _, _) => adsr

    case Tile(1, 0, _, _) => metal
    case Tile(1, 1, _, _) => filter

    case _ => EmptySoundExperiment
  }

  case object EmptySoundExperiment extends SoundExperiment {

    def title: String = ""

    def start(nienth: Nineth): Unit = ()

    def stop(): Unit = ()

  }

}
