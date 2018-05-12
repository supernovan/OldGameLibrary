package Environments

import Painting.Panel
import java.awt.image.BufferedImage
import java.util.concurrent.{Callable, FutureTask, RunnableFuture}
import javax.swing.{SwingUtilities, Timer}
import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}
import java.util

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import java.util.HashMap

import scala.util.Random

class TwentyFortyEight(window: Panel) extends GameEnvironment {

  override type A = (Double, Double)
  val board = Array.ofDim[Int](4, 4)
  val colorMap = new util.HashMap[Int, java.awt.Color]()
  val rand = new Random()





  override def init(): Unit = {

    colorMap.put(0, Color.LIGHT_GRAY)
    var nbr = 2
    val startValue = 200
    var index = 0
    while (nbr <= 4096) {
      colorMap.put(nbr, new java.awt.Color(255, 255, startValue - index*17))
      index += 1
      nbr *= 2
    }

    newBrick()
    updatePaint()

  }

  private def newBrick(): Unit = {
    var x = 0
    var y = 0
    do {
      x = rand.nextInt(4)
      y = rand.nextInt(4)
    } while (board(x)(y) != 0)

    println("bläh")
    if (rand.nextInt(10) == 0) {
      board(x)(y) = 4
    } else {
      board(x)(y) = 2
    }

  }
  override def timeFrame(input: Int): Unit = {

    val flag = moveDirection(input)
    if (flag) {
      println("jadu")
      newBrick()
      paintBoard()
      window.updatePaint()
    }


  }

  private def moveDirection(input: Int): Boolean = {
    var moveLength = 0
    var flag = false
    input match {
      case 1 => {
        for (i <- 0 until 4) {
          moveLength = 0
          for (j <- 0 until 4) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {
              board(i)(j-moveLength) = board(i)(j)
              if (moveLength > 0) {
                flag = true
              }
            }
          }
        }

      }
      case 5 => {
        for (i <- 0 until 4) {
          moveLength = 0
          for (j <- 3 to 0 by -1) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {
              board(i)(j + moveLength) = board(i)(j)
              if (moveLength > 0) {
                flag = true
              }
            }
          }
        }
      }
      case 2 => {
        for (j <- 0 until 4) {
          moveLength = 0
          for (i <- 3 to 0 by -1) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {
              board(i + moveLength)(j) = board(i)(j)
              if (moveLength > 0) {
                flag = true
              }
            }
          }
        }
      }
      case 3 => {
        for (i <- 0 until 4) {
          moveLength = 0
          for (j <- 0 until 4) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {
              board(i)(j - moveLength) = board(i)(j)
              if (moveLength > 0) {
                flag = true
              }
            }
          }
        }
      }
    }
    flag
  }

  private def mergeBricks(input: Int): Boolean = {
    var flag = false



    flag
  }


  override def getInput(): Int = {
    window.waitForKeyInput()

  }

  override def mouseModeInput(input: (Int, Int)): Unit = {

  }

  def getMouseInput(): (Int, Int) = {
    val coord = window.waitForMouseClick()


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
        board(i)(j) = 0
      }
    }
    paintBoard()


  }

  override def updatePaint(): Unit = {
    paintBoard()
    window.updatePaint()
  }



  private def paintBoard(): Unit = {
    window.resetWindow()
    val blockSize = 150
    val xOrigin = 100
    val yOrigin = 100

    for (i <- 0 to 4) {
      window.moveTo(xOrigin + i*blockSize, yOrigin)
      window.lineTo(xOrigin + i*blockSize, 700)
    }

    for (i <- 0 to 4) {
      window.moveTo(xOrigin, yOrigin + i*blockSize)
      window.lineTo(700, yOrigin + i*blockSize)
    }




    for (i <- 0 until board.length) {
      for (j <- 0 until board.length) {
        window.createRectangle(xOrigin + i*blockSize + 1, yOrigin +(j+1)*blockSize -1, xOrigin + (i+1)*blockSize - 1, yOrigin +(j+1)*blockSize -1, blockSize-2, colorMap.get(board(i)(j)))
        if (board(i)(j) != 0) {
          window.addString(board(i)(j).toString, xOrigin + i*blockSize + blockSize/2, yOrigin + j*blockSize + blockSize/2)
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
}

