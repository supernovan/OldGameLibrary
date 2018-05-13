import Painting.Panel
import Main.ReinLib


object TestWindowSwing {
  def printArr(test: Array[Array[Int]]): Unit = {
    for (i <- 0 until 4) {
      for (j <- 0 until 4) {
        print(test(i)(j) + " ")
      }
      print("\n")
    }
    println(" ------- ")
  }

  def main(args: Array[String]) {


    val test = Array.ofDim[Int](4, 4)

    test(0)(0) = 2
    test(0)(1) = 2
    test(0)(2) = 4
    test(0)(3) = 4



    var prevBrick = 0


    for (i <- 0 until 4) {
      prevBrick = 0
      for (j <- 3 to 0 by -1) {
        if (prevBrick != 0 && prevBrick == test(i)(j)) {
          test(i)(j+1) *= 2
          test(i)(j) = 0
          for (k <- j until 0 by -1) {
            test(i)(k) = test(i)(k-1)
          }
          test(i)(0) = 0
          prevBrick = test(i)(j)
          printArr(test)
        } else {
          prevBrick = test(i)(j)
        }
      }

    }



  }


}
