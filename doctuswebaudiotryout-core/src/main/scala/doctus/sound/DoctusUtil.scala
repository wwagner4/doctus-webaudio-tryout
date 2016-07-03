package doctus.sound

/**
  * Created by wwagner4 on 03/07/16.
  */
object DoctusUtil {

  def map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double) = {
    require(value >= start1)
    require(value <= stop1)
    require(start1 <= stop1)
    require(start2 <= stop2)

    (stop2 - start2) * (value - start1) / (stop1 - start1) + start2
  }

}
