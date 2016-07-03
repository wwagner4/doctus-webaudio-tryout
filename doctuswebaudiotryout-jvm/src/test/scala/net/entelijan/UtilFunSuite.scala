package net.entelijan

import doctus.sound.DoctusUtil
import org.scalatest.FunSuite

class UtilFunSuite extends FunSuite {

  test("map_01") {
    assert(DoctusUtil.map(10, 10, 20, 0, 1) === 0)
  }

  test("map_02") {
    assert(DoctusUtil.map(20, 10, 20, 0, 1) === 1)
  }

  test("map_03") {
    assert(DoctusUtil.map(15, 10, 20, 0, 1) === 0.5)
  }

  test("map_04") {
    assert(DoctusUtil.map(17, 10, 20, 0, 1) === 0.7)
  }

  test("map_05") {
    assert(DoctusUtil.map(10, 10, 20, 0, 100) === 0)
  }

  test("map_06") {
    assert(DoctusUtil.map(20, 10, 20, 0, 100) === 100)
  }

  test("map_07") {
    assert(DoctusUtil.map(15, 10, 20, 0, 100) === 50)
  }

  test("map_08") {
    assert(DoctusUtil.map(17, 10, 20, 0, 100) === 70)
  }

  test("map_09") {
    assert(DoctusUtil.map(10, 10, 20, 10, 110) === 10)
  }

  test("map_10") {
    assert(DoctusUtil.map(20, 10, 20, 10, 110) === 110)
  }

  test("map_11") {
    assert(DoctusUtil.map(15, 10, 20, 10, 110) === 60)
  }

  test("map_12") {
    assert(DoctusUtil.map(17, 10, 20, 10, 110) === 80)
  }

  test("map_13") {
    assert(DoctusUtil.map(10, 10, 20, -10, 90) === -10)
  }

  test("map_14") {
    assert(DoctusUtil.map(20, 10, 20, -10, 90) === 90)
  }

  test("map_15") {
    assert(DoctusUtil.map(15, 10, 20, -10, 90) === 40)
  }

  test("map_16") {
    assert(DoctusUtil.map(17, 10, 20, -10, 90) === 60)
  }


}