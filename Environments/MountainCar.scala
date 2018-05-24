package Environments

import Painting.Panel
import java.awt.image.BufferedImage
import java.util.concurrent.{Callable, FutureTask, RunnableFuture}
import javax.swing.SwingUtilities

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag


class MountainCar(window: Panel) extends GameEnvironment {
  override type A = (Double, Double)

  var curX: Double = 275
  var curV: Double = 0
  var gameState = false
  val wallValue = 28

  override def init(): Unit = {
    mountCarPaint()
    paintCar(275)
    updatePaint()
  }
  override def timeFrame(input: Int): Unit = {
    val driveConst = 0.27
    var inputNew = 0
    inputNew = if (input == 2) 1 else if (input == 4) -1 else 0

    if (wallValue < curX && curX < 726) {
      curV += inputNew * driveConst + Math.cos(curX / 145.0 - 50 / 145.0)
      curX += curV

      //    println("Position: " + curX + " Hastighet: " + curV)
      //    println("derivata: " + Math.cos(curX/145.0 - 50/145.0))
      window.resetWindow()
      paintCar(curX)
    } else if (curX < wallValue) {
      curX = wallValue + 1
      if (curV < 0) {
        curV *= -1*2/3.0
      }
      window.resetWindow()
      paintCar(curX)
    } else {
      gameState = true
//      reset()
    }
    mountCarPaint()
    updatePaint()
  }

  override def getInputSpace(state: A): ArrayBuffer[Int] = ArrayBuffer(2, 4)

  override def getInput(): Int = {
    window.waitForKeyInput()
  }

  override def mouseModeInput(input: (Int, Int)): Unit = {

  }

  def getMouseInput(): (Int, Int) = {
    window.waitForMouseClick()
  }

  override def getFrame(): BufferedImage = {
    window.getFrame()
  }

  override def releaseLock(): Unit = {
    window.releaseOne()
  }

  override def reset(): Unit = {
    window.resetWindow()
    paintCar(275)
    //mountCarPaint()

    curX = 275
    curV = 0
    gameState = false
  }

  override def updatePaint(): Unit = {
    window.updatePaint()
  }


  def mountCarPaint(): Unit = {
    //    window.moveTo(0, (150.0*Math.sin(0/145.0 - 50.0/145.0) + 500).toInt)
    //    for (x <- 0 until 1000) {
    //      val y1 = 150.0*Math.sin(x/145.0 - 50.0/145.0) + 500
    //      window.lineTo(x, Math.round(y1).toInt)
    //    }

    window.moveTo(0, (150.0*Math.sin(0.0/145.0 - 50.0/145.0) + 520).toInt)
    for (x <- 0 until 1000) {
      val y1: Double = 150.0*Math.sin(x.toDouble/145.0 - 50.0/145.0) + 500
      val der = 150.0/145.0*Math.cos(x.toDouble/145.0 - 50.0/145.0)

      window.lineTo((x + 20*Math.cos(Math.PI -Math.atan2(1.0, der))).toInt, (y1 - 20*Math.sin(Math.PI + Math.atan2(1.0, der))).toInt)
      //      println(Math.atan2(1.0, -der) + " grader: " + Math.toDegrees(Math.atan2(1.0, -der)))
    }

    window.moveTo(735, 370)
    window.lineTo(735, 340)
    window.createRectangle(725, 350, 735, 350, 10, java.awt.Color.RED)
    //    window.drawCurve(0, 450, 275, 830, 518, 50)
    //    window.drawCurve(518, 489, 733, 351, 800, 365)

  }

  private def paintCar(x: Double): Unit = {
    val xDiff = 15.0
    val y1: Double = 150.0*Math.sin((x-xDiff)/145.0 - 50.0/145.0) + 500
    val y2: Double = 150.0*Math.sin((x+xDiff)/145.0 - 50.0/145.0) + 500
    //println("y1 är: " + y1)
    val a1 = Math.asin(Math.sin((x-xDiff).toDouble/145.0 - 50.0/145.0))
    val a2 = Math.asin(Math.sin((x+xDiff).toDouble/145.0 - 50.0/145.0))

    //    println(Math.sin((x-xDiff).toDouble/145.0 - 50.0/145.0) + " " + Math.sin((x+xDiff).toDouble/145.0-50.0/145.0))


    makeWheels(x.toInt, xDiff.toInt)



    val wheels = window.getCircleChoords()

    val x1c = wheels(0)._1
    val x2c = wheels(1)._1
    val y1c = wheels(0)._2
    val y2c = wheels(1)._2

    window.createRectangle(x1c, y1c, x2c, y2c, 20, java.awt.Color.GREEN)

    //window.createRectangle(Math.round(x-xDiff-9).toInt, Math.round(y1).toInt+7, Math.round(x+xDiff+9).toInt, Math.round(y2).toInt+7, 20)


    //drawRec(x-xDiff, y1-35, x+xDiff+ 20, y2-35)
  }

  private def makeWheels(x: Int, diff: Int): Unit = {
    val y1 = 150.0*Math.sin((x-diff).toDouble/145.0 - 50.0/145.0) + 500
    val y2 = 150.0*Math.sin((x+diff).toDouble/145.0 - 50.0/145.0) + 500

    val x1 = (x-diff + ( 20*Math.cos(Math.PI - Math.atan2(1.0, der(x-diff))))/2).toInt
    val y1New = (y1 - (20*Math.sin(Math.PI + Math.atan2(1.0, der(x-diff))))/2).toInt

    val x2 = (x+diff + ( 20*Math.cos(Math.PI - Math.atan2(1.0, der(x+diff))))/2).toInt
    val y2New = (y2 - (20*Math.sin(Math.PI + Math.atan2(1.0, der(x+diff))))/2).toInt

    window.middleCircle(x1, y1New, 10, java.awt.Color.LIGHT_GRAY)
    window.middleCircle(x2, y2New, 10, java.awt.Color.LIGHT_GRAY)
  }

  private def der(x: Int): (Double) = {
    150.0/145.0*Math.cos(x/145.0 - 50.0/145.0)

  }

  def getState(): ((Double, Double), Boolean) = {
//    val state: RunnableFuture[Boolean] = new FutureTask[Boolean](new Callable[Boolean]() {
//      override def call() {
//
//        gameState
//      }
//    })
//    SwingUtilities.invokeLater(state)
    var state = ((0.0, 0.0), false)
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        state = ((curX, curV), gameState)
      }
    })
    state
//    ((0,0), state.get())
  }

  def nextState(state: (Double, Double), input: Int): ((Double, Double), Boolean) = {


    (state, gameState)
  }
}
