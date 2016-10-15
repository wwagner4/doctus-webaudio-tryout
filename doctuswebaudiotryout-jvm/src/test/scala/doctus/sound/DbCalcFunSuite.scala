package doctus.sound

import org.scalatest.FunSuite

/**
  * Created by wolfi on 15.10.16.
  */
class DbCalcFunSuite extends FunSuite {

  import org.scalatest.Matchers._

  val dbValues = List(20, 10, 1, 0).map(_.toDouble)
  val linValues = List(10.0, 3.162, 1.122, 1.0)

  dbValues.zip(linValues).foreach {
    case (db: Double, lin: Double) =>
      test(f"test lin to db $db%.2f $lin%.2f") {
        val calculated: Double = DbCalc.lin2db(lin)
        assert(calculated === db +- 0.003)
      }
  }

  dbValues.zip(linValues).foreach {
    case (db: Double, lin: Double) =>
      test(f"test db to lin $db%.2f $lin%.2f") {
        val calculated: Double = DbCalc.db2lin(db)
        assert(calculated === lin +- 0.01)
      }
  }

}
