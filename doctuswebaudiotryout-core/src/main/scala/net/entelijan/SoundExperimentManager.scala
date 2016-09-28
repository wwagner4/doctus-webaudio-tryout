package net.entelijan

import doctus.sound.DoctusSoundAudioContext
import net.entelijan.experiments._

/**
  * Manager for sound experiments. Makes them appear and run on the gui.
  */
case class SoundExperimentManager(soundContext: DoctusSoundAudioContext) {

  lazy val tinnitus = TinnitusExperiment(soundContext)
  lazy val melody = MelodyExperiment(soundContext)
  lazy val noise = NoiseExperiment(soundContext)
  lazy val adsr = AdsrExperiment(soundContext)
  lazy val metal  = MetalExperiment(soundContext)
  lazy val filter = FilterExperiment(soundContext)
  lazy val dynamicFilter = WhaWhaExperiment(soundContext)
  lazy val panning = PanningExperiment(soundContext)
  lazy val delay = DelayExperiment(soundContext)
  lazy val fmSynth = FmSynthExperiment(soundContext)
  lazy val ringModulation = RingModulationExperiment(soundContext)
  lazy val karplusStrong = KarplusStrongExperiment(soundContext)
  lazy val modalSynthesis = ModalSynthesisExperiment(soundContext)

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
    case Tile(2, 1, _, _) => ringModulation
    case Tile(2, 2, _, _) => karplusStrong
    case Tile(2, 3, _, _) => dynamicFilter

    case Tile(3, 0, _, _) => modalSynthesis
    case Tile(3, 1, _, _) => EmptySoundExperiment
    case Tile(3, 2, _, _) => EmptySoundExperiment
    case Tile(3, 3, _, _) => EmptySoundExperiment

    case _ => EmptySoundExperiment

  }

  case object EmptySoundExperiment extends SoundExperiment {

    def title: String = ""

    def start(nienth: Nineth): Unit = ()

    def stop(): Unit = ()

  }

}
