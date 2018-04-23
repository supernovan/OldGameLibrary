package AI

object Test {

  var nbr = 0
  def main(args: Array[String]): Unit = {
    val x = 1
    recursiv(x)

    println(nbr)

  }

  def recursiv(x: Int): Unit = {
    helper(4, 6)
  }
  private def helper(first: Int, second: Int): Unit = {
    if (first == 2018) {
      nbr += 1
    } else if (first < 2018) {
      helper(first + 3, first + 5)
    }

    if (second == 2018) {
      nbr += 1
    } else if (second < 2018) {
      helper(second + 3, second + 5)
    }
  }
}
