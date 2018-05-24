package Environments

import Painting.Panel
import java.awt.image.BufferedImage
import java.util.concurrent.{Callable, FutureTask, RunnableFuture}
import javax.swing.{SwingUtilities, Timer}
import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag


class GameOfLife(window: Panel) extends GameEnvironment {

  var animation = false

  val GameAnimationLoop = new Timer(1000/30, new ActionListener() {
    def actionPerformed(e: ActionEvent): Unit = {
      timeFrame(0)
    }
  })
  override type A = (Double, Double)
  val board = Array.ofDim[Boolean](30, 30)
  init
  override def init(): Unit = {
    updatePaint()

  }
  override def timeFrame(input: Int): Unit = {
    val tempBoard = Array.ofDim[Boolean](30, 30)

    for (i <- 0 until board.length) {
      for (j <- 0 until board(i).length) {
        val neighbours = getNeighbours(i, j)
        if (neighbours == 3) {
          tempBoard(i)(j) = true
        } else if (neighbours == 2 && board(i)(j)) {
          tempBoard(i)(j) = true
        }
      }
    }

    for (i <- 0 until board.length) {
      for (j <- 0 until board(i).length) {
        board(i)(j) = tempBoard(i)(j)
      }
    }
    paintBoard()
    window.updatePaint()
  }

  private def getNeighbours(x: Int, y: Int): Int = {
    var neighbours = 0
    for (i <- x-1 to x+1) {
      for (j <- y-1 to y+1) {
        if (i < 0 || j <0 || i >= board.length || j >= board.length) {

        } else {
          if (board(i)(j)) {
            neighbours += 1
          }
        }
      }
    }
    if (board(x)(y)) {
      neighbours -= 1
    }
    neighbours
  }

  override def getInput(): Int = {
    val coord = window.waitForMouseClick()

    println(coord._1/30)
    if (coord._1 > 100 && coord._1 < 700 && coord._2 > 100 && coord._2 < 700 ) {
      val newC = (coord._1 - 100, coord._2 - 100)
      board(newC._1/20)(newC._2/20) = if (board(newC._1/20)(newC._2/20)) false else true
    }


    if (coord._1 > 100 && coord._1 < 160 && coord._2 > 725 && coord._2 < 750) {
      timeFrame(0)
    } else if (coord._1 > 200 && coord._1 < 260 && coord._2 > 725 && coord._2 < 750) {
      if (!animation) {
        animation = true
        GameAnimationLoop.start()
      } else {
        animation = false
        GameAnimationLoop.stop()
      }
    } else if (coord._1 > 300 && coord._1 < 360 && coord._2 > 725 && coord._2 < 750) {
      reset()
    } else if (coord._1 > 400 && coord._1 < 460 && coord._2 > 725 && coord._2 < 750) {
      System.exit(0)
    }

    updatePaint()
    0
  }

  override def mouseModeInput(input: (Int, Int)): Unit = {

  }

  def getMouseInput(): (Int, Int) = {
    val coord = window.waitForMouseClick()

    println(coord._1/30)
    if (coord._1 > 100 && coord._1 < 700 && coord._2 > 100 && coord._2 < 700 ) {
      val newC = (coord._1 - 100, coord._2 - 100)
      board(newC._1/20)(newC._2/20) = if (board(newC._1/20)(newC._2/20)) false else true
    }

    updatePaint()
    coord
  }

  override def getFrame(): BufferedImage = {
    window.getFrame()
  }

  override def releaseLock(): Unit = {
    window.releaseOne()
  }

  override def reset(): Unit = {
    for (i <- 0 until board.length) {
      for (j <- 0 until board(i).length) {
        board(i)(j) = false
      }
    }
    paintBoard()


  }

  override def updatePaint(): Unit = {
    paintBoard()
    window.updatePaint()
  }


  def mountCarPaint(): Unit = {

  }

  private def paintBoard(): Unit = {
    window.resetWindow()
    val blockSize = 20
    val xOrigin = 100
    val yOrigin = 100

    for (i <- 0 to 30) {
      window.moveTo(xOrigin + i*blockSize, yOrigin)
      window.lineTo(xOrigin + i*blockSize, 700)
    }

    for (i <- 0 to 30) {
      window.moveTo(xOrigin, yOrigin + i*blockSize)
      window.lineTo(700, yOrigin + i*blockSize)
    }

    //buttons

    window.createRectangle(100, 750, 160, 750, 25, Color.YELLOW)
    window.createRectangle(200, 750, 260, 750, 25, Color.BLUE)
    window.createRectangle(300, 750, 360, 750, 25, Color.RED)
    window.createRectangle(400, 750, 460, 750, 25, Color.BLACK)





    for (i <- 0 until board.length) {
      for (j <- 0 until board.length) {
        if (board(i)(j)) {
          window.createRectangle(xOrigin + i*blockSize + 1,yOrigin + (j+1)*blockSize - 1 ,xOrigin + (i+1)*blockSize -1, yOrigin + (j+1)*blockSize - 1, blockSize -2, Color.GREEN)
        }
      }
    }

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
//        state = ((curX, curV), gameState)
      }
    })
    state
    //    ((0,0), state.get())
  }

  def nextState(state: (Double, Double), input: Int): ((Double, Double), Boolean) = {


    (state, true)
  }

  override def getInputSpace(state: A): ArrayBuffer[Int] = ArrayBuffer.empty[Int]
}

