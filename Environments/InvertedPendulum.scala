package Environments

import java.awt.image.BufferedImage
import javax.swing.SwingUtilities

import Painting.Panel

import scala.collection.mutable.ArrayBuffer

class InvertedPendulum(window: Panel) extends GameEnvironment {
  type A = (Double, Double)

  var xBall = 0.0
  var yBall = 0.0

  var xCart = 400.0
  val yCart = 580.0
  var cartVel = 0.0
  val widthCart = 30

  var theta = 0.0
  var thetaDot = 0.0

  val length = 60.0
  val m = 0.9
  val g = 9.82

  var gameState = false
  var side = 0
  def init(): Unit = {
    window.moveTo(0, 600)
    window.lineTo(800, 600)





    initState()


    updatePaint()
  }

  private def initState(): Unit = {
    val rand = new scala.util.Random()
    var xDiff = 0
    while (xDiff == 0) {
      xDiff = rand.nextInt(5) - 2
    }

    xBall = xCart + xDiff
    println(xBall + " " + xCart)
    val angle = Math.asin((xBall - xCart).toDouble/length)
    println(Math.toDegrees(angle))
    yBall = yCart - length*Math.cos(angle)

    window.createRectangle(370, 620, 430, 620, 40, java.awt.Color.GRAY)
    window.createRectangle(398 + Math.sin(angle) , yCart+Math.sin(angle), 402 - Math.sin(angle), yCart - Math.sin(angle), 60)
    window.middleCircle(Math.round(xBall).toInt, Math.round(yBall).toInt, 10, java.awt.Color.RED)
    theta = Math.asin((xBall - xCart).toDouble/length)
  }


  //input = 1 is right, -1 is left, 0 is nothing
  def timeFrame(input: Int): Unit = {

    val newInput = if (input == 2) 1 else if (input == 4) -1 else 0

    val angle = Math.asin((xCart - xBall).toDouble/length)

    if (angle.isNaN && gameState) {
      gameState = false
      side = if (xBall > xCart) -1 else 1
    }
    cartVel += newInput
    if (cartVel > 5) {
      cartVel = 5
    } else if (cartVel < -5) {
      cartVel = -5
    }

    if (newInput == 0) {
      cartVel *= 0.8
    }
    println(xCart - xBall)
    println("diff" + g*m*(Math.sin(angle)))
    xBall += (-cartVel*Math.cos(angle) - g*m*(Math.sin(angle)))
    xCart += cartVel
    var newAngle = Math.asin((xCart - xBall).toDouble/length)
    if (newAngle.isNaN && gameState) {
      gameState = false
      side = if (xBall > xCart) -1 else 1
    }
    yBall = yCart - length*Math.cos(newAngle)

    reset()
    thetaDot = newAngle - theta
    theta = newAngle

    if (!gameState) {
      if (side > 0) paintPendulum(Math.PI/2) else paintPendulum(-Math.PI/2)
    } else {
      paintPendulum(newAngle)
    }
  }

  private def paintPendulum(angle: Double): Unit = {

    window.moveTo(0, 600)
    window.lineTo(800, 600)


    window.createRectangle(xCart - widthCart, 620, xCart + widthCart, 620, 40, java.awt.Color.GRAY)
    window.createRectangle(xCart - 2*Math.cos(angle) , yCart+2*Math.sin(angle), xCart + 2*Math.cos(angle), yCart - Math.sin(angle), 60)

    if (!gameState) {
      window.middleCircle(Math.round(xCart - 60*side).toInt, Math.round(yCart).toInt, 10, java.awt.Color.RED)
    } else {
      window.middleCircle(Math.round(xBall).toInt, Math.round(yBall).toInt, 10, java.awt.Color.RED)
    }


    updatePaint()
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
        state = ((theta, thetaDot), gameState)
      }
    })
    state
    //    ((0,0), state.get())
  }

  def reset(): Unit = {
    window.resetWindow()
  }

  def getInput(): Int = {
    window.waitForKeyInput()
  }

  def getMouseInput(): (Int, Int) = {
    window.waitForMouseClick()
  }

  override def mouseModeInput(input: (Int, Int)): Unit = {

  }

  def getFrame(): BufferedImage = {
    window.getFrame()
  }

  def releaseLock(): Unit = {
    window.releaseOne()
  }

  def updatePaint(): Unit = {
    window.updatePaint()
  }

  private def paintPendulum(): Unit = {

  }
}
