package doctus.sound

/**
  * ScalaJS implementation of the (experimental sound interface)
  */
class DoctusSoundJs extends DoctusSound {

  override def noteOn: Unit = println("noteOn")

  override def noteOff: Unit = println("noteOff")

}
