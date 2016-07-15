package net.entelijan

import doctus.sound.DoctusUtil
import org.scalatest.FunSuite

class SoundUtilFunSuite extends FunSuite {

  import net.entelijan.SoundUtil._

  case class T1(index: Int, value: Double)

  {
    val realVals = metalHarmonics(100, 5)
    test("metalHarmonicsSize") {
      assert(realVals.size === 5)
    }
      
    val testVals = List(
      T1(0, 141.2),
      T1(1, 200.0),
      T1(2, 282.8),
      T1(3, 400.0),
      T1(4, 565.6)) 
  
    testVals.foreach { t => 
      val prec = 0.5
      test(s"metalHarmonics_$t") {
        assert(realVals(t.index) > t.value - prec) 
        assert(realVals(t.index) < t.value + prec) 
      }   
    }
  }
  
  {
    val realVals = (0 to 5).toList.map { x => logDecay(1.2)(x) }
    val testVals = List(
      T1(0, 1.0), 
      T1(1, 0.83), 
      T1(2, 0.69), 
      T1(3, 0.57), 
      T1(4, 0.48), 
      T1(5, 0.40))
  
    testVals.foreach { t => 
      val prec = 0.1
      test(s"logDecay_1.2_$t") {
        assert(realVals(t.index) > t.value - prec) 
        assert(realVals(t.index) < t.value + prec) 
      }   
    }
  }
  
  {
    val realVals = (0 to 5).toList.map { x => logDecay(2)(x) }
    val testVals = List(
      T1(0, 1.0), 
      T1(1, 0.5), 
      T1(2, 0.25), 
      T1(3, 0.125), 
      T1(4, 0.0625), 
      T1(5, 0.03125))

    testVals.foreach { t => 
      val prec = 0.1
      test(s"logDecay_2.0_$t") {
        assert(realVals(t.index) > t.value - prec) 
        assert(realVals(t.index) < t.value + prec) 
      }   
    }
  }
  
  {
    val realVals = (0 to 5).toList.map { x => logDecay(4)(x) }
    val testVals = List(
      T1(0, 1.0), 
      T1(1, 0.25), 
      T1(2, 0.0625), 
      T1(3, 0.015625), 
      T1(4, 0.00390625))

    testVals.foreach { t => 
      val prec = 0.1
      test(s"logDecay_4.0_$t") {
        assert(realVals(t.index) > t.value - prec) 
        assert(realVals(t.index) < t.value + prec) 
      }   
    }
  }
    
}

