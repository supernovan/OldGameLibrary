package Environments

import java.awt.image.BufferedImage

import Painting.Panel

import scala.collection.mutable.ArrayBuffer
import Array._
import java.awt.{Color, Toolkit}
import java.util.concurrent.{Callable, FutureTask}
import javax.swing.SwingUtilities


class RushHour(window: Panel) extends GameEnvironment() {
  type A = (Int, Int, Int)

  val grid = Array.ofDim[Int](6, 6)
  var gameState = false
  var redCar = ((0, 2), (1, 2))
  var otherCars = ArrayBuffer.empty[(Array[(Int, Int)], Int)]
  var currentCar = -1

  val exit = (6, 2)
  /*
    DIRECTION

        1
      4   2
        3
   */

  def init(): Unit = {
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        paintGrid()
        initFirstMap()
        //fillGrid()
        window.updatePaint()
      }
    })

  }

  private def initFirstMap(): Unit = {

    var x1 = 151 + redCar._1._1*50
    var x2 = x1 + 48
    var y1 = 199 + redCar._1._2 * 50
    var y2 = y1
    window.createRectangle(x1, y1, x2, y2, 48, Color.RED)

    x1 = 151 + redCar._2._1*50
    x2 = x1 + 48
    y1 = 199 + redCar._2._2 * 50
    y2 = y1
    window.createRectangle(x1, y1, x2, y2, 48, Color.RED)

    x1 = 151 + exit._1*50
    x2 = x1 + 48
    y1 = 199 + exit._2 * 50
    y2 = y1
    window.createRectangle(x1, y1, x2, y2, 48, Color.YELLOW)


    val yellowCar = (Array[(Int, Int)]((5, 0), (5, 1), (5, 2)), 1)
    val purpleCar = (Array((5, 3), (4, 3)), 2)

    otherCars += yellowCar
    otherCars += purpleCar

    for (i <- otherCars) {
      for (j <- i._1) {
        val x1 = 151 + j._1 * 50
        val x2 = x1 + 48
        val y1 = 199 + j._2 * 50
        val y2 = y1
        window.createRectangle(x1, y1, x2, y2, 48, Color.BLUE)
      }
    }
  }

  private def fillGrid(): Unit = {

    var x1 = 151 + redCar._1._1
    var x2 = x1 + 48
    var y1 = 199 + redCar._1._2 * 50
    var y2 = y1
    window.createRectangle(x1, y1, x2, y2, 48, Color.RED)

    x1 = 151 + redCar._2._1
    x2 = x1 + 48
    y1 = 199 + redCar._2._2 * 50
    y2 = y1
    window.createRectangle(x1, y1, x2, y2, 48, Color.RED)


    for (i <- otherCars) {
      for (j <- i._1) {
        val x1 = 151 + j._1 * 50
        val x2 = x1 + 48
        val y1 = 199 + j._2 * 50
        val y2 = y1
        window.createRectangle(x1, y1, x2, y2, 48, Color.BLUE)
      }
    }

  }


  private def paintGrid(): Unit = {

    for (x <- 150 to 450 by 50) {
      window.moveTo(x, 150)
      window.lineTo(x, 450)
    }

    for (y <- 150 to 450 by 50) {
      window.moveTo(150, y)
      window.lineTo(450, y)
    }
  }


  def timeFrame(input: Int): Unit = {
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {

        if (currentCar != -1) {
          input match {
            case 1 =>

          }
        }


        window.resetWindow()
        paintGrid()
        fillGrid()
        window.updatePaint()
        Toolkit.getDefaultToolkit.sync()
      }
    })


    //    println(manhattanDist(snakeBody(snakeBody.length-1), applePos))
  }

  def manhattanDist(pos1: (Int, Int), pos2: (Int, Int)): Int = {
    Math.abs(pos1._1 - pos2._1) + Math.abs(pos1._2 - pos2._2)
  }


  def reset(): Unit = {
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        gameState = false
        window.resetWindow()
        paintGrid()
        otherCars.clear()
        window.updatePaint()
      }
    })
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

  override def getState(): ((Int, Int, Int), Boolean) = {
    //    val state = new FutureTask[((ArrayBuffer[(Int, Int)], (Int, Int)), Boolean)](new Callable[((ArrayBuffer[(Int, Int)], (Int, Int)), Boolean)]() {
    //      override def call() {
    //        ((snakeBody, applePos), gameState)
    //      }
    //    })
    //    SwingUtilities.invokeLater(state)
    //    state.get()
    var state: ((Int, Int, Int), Boolean) = null
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        state = ((0, 0, 0), gameState)
      }
    })
    state
  }
}
