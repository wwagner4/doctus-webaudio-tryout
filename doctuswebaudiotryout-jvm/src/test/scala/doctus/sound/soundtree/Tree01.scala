package doctus.sound.soundtree

import doctus.sound.DoctusSoundAudioContext
import doctus.sound.WaveType_Sine
import doctus.sound.FilterType_Lowpass

object Tree01 extends App {

  import DoctusSoundNodeFrameworkTreeImpl._

  println("Tree01")

  val ctx: DoctusSoundAudioContext = new DoctusSoundAudioContextTree()

  val sin = ctx.createNodeSourceOscil(WaveType_Sine)
  val filt = ctx.createNodeThroughFilter(FilterType_Lowpass)
  val out = ctx.createNodeSinkLineOut

  ctx.createNodeControlConstant(444) >- filt.frequency

  sin.start(ctx.currentTime)
  
  
}