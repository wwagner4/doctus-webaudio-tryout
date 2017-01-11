package doctus.sound.soundtree

import doctus.sound._

object DoctusSoundNodeFrameworkTreeImpl {

  class DoctusSoundAudioContextTree extends DoctusSoundAudioContext {
    def createNodeControlAdsr(attack: Double, decay: Double, sustain: Double, release: Double, gain: Double, trend: doctus.sound.Trend): doctus.sound.NodeControlEnvelope = ???
    def createNodeControlConstant(value: Double): doctus.sound.NodeControl = ???
    def createNodeControlLfo(waveType: doctus.sound.WaveType): doctus.sound.NodeControlLfo = ???
    def createNodeSinkLineOut: doctus.sound.NodeSink = ???
    def createNodeSourceNoise(noiseType: doctus.sound.NoiseType): doctus.sound.NodeSourceNoise = ???
    def createNodeSourceOscil(waveType: doctus.sound.WaveType): doctus.sound.NodeSourceOscil = ???
    def createNodeThroughDelay: doctus.sound.NodeThroughDelay = ???
    def createNodeThroughFilter(filterType: doctus.sound.FilterType): doctus.sound.NodeThroughFilter = ???
    def createNodeThroughGain: doctus.sound.NodeThroughGain = ???
    def createNodeThroughPan: doctus.sound.NodeThroughPan = ???
    def currentTime: Double = ???
    def sampleRate: Double = ???
    def terminate: Unit = ???

    class NodeSourceTree extends NodeSource {

      def connect(through: doctus.sound.NodeThrough): doctus.sound.NodeSource = ???

      def connect(sink: doctus.sound.NodeSink): Unit = ???

    }

    class NodeSinkTree extends NodeSink {

    }

    class NodeThroughTree extends NodeThrough {

      def connect(through: doctus.sound.NodeThrough): doctus.sound.NodeSource = ???
      def connect(sink: doctus.sound.NodeSink): Unit = ???

    }

    class NodeThroughFilterTree extends NodeThroughTree {

      def frequency: doctus.sound.ControlParam = ???
      def quality: doctus.sound.ControlParam = ???

    }

    class NodeControlTree extends NodeControl {
      def connect(param: ControlParam): Unit = ???
    }

  }

}