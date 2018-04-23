import Painting.Panel

object TestWindowSwing {
  val window = new Panel(800, 800)
  def main(args: Array[String]) {


    window.moveTo(0, (150.0*Math.sin(0/145.0 - 50.0/145.0) + 500).toInt)
    for (x <- 0 until 1000) {
      val y1: Double = 150.0*Math.sin(x.toDouble/145.0 - 50.0/145.0) + 500
      window.lineTo(x, Math.round(y1).toInt)
    }

    window.moveTo(0, (150.0*Math.sin(0.0/145.0 - 50.0/145.0) + 520).toInt)
    for (x <- 0 until 1000) {
      val y1: Double = 150.0*Math.sin(x.toDouble/145.0 - 50.0/145.0) + 500
      val der = 150.0/145.0*Math.cos(x.toDouble/145.0 - 50.0/145.0)

      window.lineTo((x + 20*Math.cos(Math.PI -Math.atan2(1.0, der))).toInt, (y1 - 20*Math.sin(Math.PI + Math.atan2(1.0, der))).toInt)
      println(Math.atan2(1.0, -der) + " grader: " + Math.toDegrees(Math.atan2(1.0, -der)))
    }




    makeShapes(275, 15)
    makeShapes(450, 15)

    window.updatePaint()
  }

  def der(x: Int): Double = {
    150.0/145.0*Math.cos(x.toDouble/145.0 - 50.0/145.0)
  }

  def makeShapes(x: Int, diff: Int): Unit = {
    val y1 = 150.0*Math.sin((x-diff).toDouble/145.0 - 50.0/145.0) + 500
    val y2 = 150.0*Math.sin((x+diff).toDouble/145.0 - 50.0/145.0) + 500

    val x1 = (x-diff + ( 20*Math.cos(Math.PI - Math.atan2(1.0, der(x-diff))))/2).toInt
    val y1New = (y1 - (20*Math.sin(Math.PI + Math.atan2(1.0, der(x-diff))))/2).toInt

    val x2 = (x+diff + ( 20*Math.cos(Math.PI - Math.atan2(1.0, der(x+diff))))/2).toInt
    val y2New = (y2 - (20*Math.sin(Math.PI + Math.atan2(1.0, der(x+diff))))/2).toInt

    window.middleCircle(x1, y1New, 10)
    window.middleCircle(x2, y2New, 10)
  }
}
