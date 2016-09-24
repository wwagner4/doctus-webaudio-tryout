package net.entelijan

import doctus.sound.DoctusSoundAudioContext
import net.entelijan.experiments._

/**
  * Manager for sound experiments. Makes them appear and run on the gui.
  */
case class SoundExperimentManager(soundContext: DoctusSoundAudioContext) {

  lazy val tinnitus = Tinnitus(soundContext)
  lazy val melody = Melody(soundContext)
  lazy val noise = Noise(soundContext)
  lazy val adsr = AdsrTryout(soundContext)
  lazy val metal  = MetalTryout(soundContext)
  lazy val filter = FilterTryout(soundContext)
  lazy val panning = PanningExperiment(soundContext)
  lazy val delay = DelayExperiment(soundContext)
  lazy val fmSynth = FmSynthExperiment(soundContext)

  def experiment: Tile => SoundExperiment = {
    case Tile(0, 0, _, _) => tinnitus
    case Tile(0, 1, _, _) => melody
    case Tile(0, 2, _, _) => noise
    case Tile(0, 3, _, _) => adsr

    case Tile(1, 0, _, _) => metal
    case Tile(1, 1, _, _) => filter
    case Tile(1, 2, _, _) => panning
    case Tile(1, 3, _, _) => delay

    case Tile(2, 0, _, _) => fmSynth
    case Tile(2, 1, _, _) => EmptySoundExperiment
    case Tile(2, 2, _, _) => EmptySoundExperiment
    case Tile(2, 3, _, _) => EmptySoundExperiment

    case _ => EmptySoundExperiment
  }

  case object EmptySoundExperiment extends SoundExperiment {

    def title: String = ""

    def start(nienth: Nineth): Unit = ()

    def stop(): Unit = ()

  }

}
