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

  override type A = (Array[Array[Int]])
  val boardOriginal = Array.ofDim[Int](4, 4)
  val colorMap = new util.HashMap[Int, java.awt.Color]()
  val rand = new Random()
  var gameState = true
  var score = 0



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

    newBrick(boardOriginal)
    updatePaint()

  }

  override def getInputSpace(state: A): ArrayBuffer[Int] = {
    val arr = ArrayBuffer.empty[Int]

    for (i <- 1 to 4) {
      val temp = copyBoard(state)
      if (moveDirection(i, temp)) {
        arr += i
      }
    }


    arr
  }

  private def newBrick(board: Array[Array[Int]]): Unit = {
    var flag = true
    for (x <- 0 until 4 if flag) {
      for (y <- 0 until 4 if flag) {
        if (board(x)(y) == 0) {
          flag = false
        }
      }
    }
    if (!flag) {
      var x = 0
      var y = 0
      do {
        x = rand.nextInt(4)
        y = rand.nextInt(4)
      } while (board(x)(y) != 0)

      if (rand.nextInt(10) == 0) {
        board(x)(y) = 4
      } else {
        board(x)(y) = 2
      }
    }


  }

  private def copyBoard(board: Array[Array[Int]]): Array[Array[Int]] = {
    val temp = Array.ofDim[Int](4, 4)
    for (i <- 0 until 4) {
      for (j <- 0 until 4) {
        temp(i)(j) = board(i)(j)
      }
    }
    temp
  }
  override def timeFrame(input: Int): Unit = {



    val flag = moveDirection(input, boardOriginal)
    if (flag) {
      newBrick(boardOriginal)
      paintBoard()
      window.updatePaint()
    }




    if (!checkGameState(boardOriginal)) {
      gameState = false
    }
  }

  private def checkGameState(board: Array[Array[Int]]): Boolean = {
    var check = false

    for (i <- 1 to 4) {
      val temp = copyBoard(board)
      if (moveDirection(i, temp)) {
        check = true

      }
    }

    check
  }


  private def moveDirection(input: Int, board:  Array[Array[Int]]): Boolean = {
    var moveLength = 0
    var flag = false
    input match {
      case 1 => {
        for (j <- 0 until 4) {
          moveLength = 0
          for (i <- 0 until 4) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {
              if (moveLength > 0) {
                flag = true
                board(i-moveLength)(j) = board(i)(j)
                board(i)(j) = 0
              }
            }
          }
        }

      }
      case 2 => {
        for (i <- 0 until 4) {
          moveLength = 0
          for (j <- 3 to 0 by -1) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {

              if (moveLength > 0) {
                board(i)(j + moveLength) = board(i)(j)
                board(i)(j) = 0
                flag = true
              }
            }
          }
        }
      }
      case 3 => {
        for (j <- 0 until 4) {
          moveLength = 0
          for (i <- 3 to 0 by -1) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {
              if (moveLength > 0) {
                flag = true
                board(i+moveLength)(j) = board(i)(j)
                board(i)(j) = 0
              }
            }
          }
        }
      }
      case 4 => {
        for (i <- 0 until 4) {
          moveLength = 0
          for (j <- 0 until 4) {
            if (board(i)(j) == 0) {
              moveLength += 1
            } else {

              if (moveLength > 0) {
                board(i)(j - moveLength) = board(i)(j)
                board(i)(j) = 0
                flag = true
              }
            }
          }
        }
      }
    }

//    for (i <- 0 until 4) {
//      for (j <- 0 until 4) {
//        print(board(i)(j) + " ")
//      }
//      print("\n")
//    }
    val flagTwo = mergeBricks(input, board)
    flag || flagTwo
  }

  private def mergeBricks(input: Int, board:  Array[Array[Int]]): Boolean = {
    var flag = false
    var prevBrick = 0
    input match {
      case 1 => {
        for (j <- 0 until 4) {
          prevBrick = 0
          for (i <- 0 until 4) {
            if (prevBrick != 0 && prevBrick == board(i)(j)) {
              board(i-1)(j) *= 2
              board(i)(j) = 0
              flag = true
              for (k <- i until 3) {
                board(k)(j) = board(k+1)(j)
              }
              board(3)(j) = 0
              prevBrick = board(i)(j)
            } else {
              prevBrick = board(i)(j)
            }
          }
        }

      }
      case 2 => {
        for (i <- 0 until 4) {
          prevBrick = 0
          for (j <- 3 to 0 by -1) {
            if (prevBrick != 0 && prevBrick == board(i)(j)) {
              board(i)(j+1) *= 2
              board(i)(j) = 0
              flag = true
              for (k <- j until 0 by -1) {
                board(i)(k) = board(i)(k-1)
              }
              board(i)(0) = 0
              prevBrick = board(i)(j)
            } else {
              prevBrick = board(i)(j)
            }
          }
        }
      }
      case 3 => {
        for (j <- 0 until 4) {
          prevBrick = 0
          for (i <- 3 to 0 by -1) {
            if (prevBrick != 0 && prevBrick == board(i)(j)) {
              board(i+1)(j) *= 2
              board(i)(j) = 0
              flag = true
              for (k <- i until 0 by -1) {
                board(k)(j) = board(k-1)(j)
              }
              board(0)(j) = 0
              prevBrick = board(i)(j)
            } else {
              prevBrick = board(i)(j)
            }
          }
        }
      }
      case 4 => {
        for (i <- 0 until 4) {
          prevBrick = 0
          for (j <- 0 until 4) {
            if (prevBrick != 0 && prevBrick == board(i)(j)) {
              board(i)(j-1) *= 2
              board(i)(j) = 0
              flag = true
              for (k <- j until 3) {
                board(i)(k) = board(i)(k+1)
              }
              board(i)(3) = 0
              prevBrick = board(i)(j)
            } else {
              prevBrick = board(i)(j)
            }
          }
        }
      }
    }

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
    for (i <- 0 until boardOriginal.length) {
      for (j <- 0 until boardOriginal(i).length) {
        boardOriginal(i)(j) = 0
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




    for (i <- 0 until boardOriginal.length) {
      for (j <- 0 until boardOriginal.length) {
        window.createRectangle(xOrigin + j*blockSize + 1, yOrigin +(i+1)*blockSize -1, xOrigin + (j+1)*blockSize - 1, yOrigin +(i+1)*blockSize -1, blockSize-2, colorMap.get(boardOriginal(i)(j)))
        if (boardOriginal(i)(j) != 0) {
          window.addString(boardOriginal(i)(j).toString, xOrigin + j*blockSize + blockSize/2, yOrigin + i*blockSize + blockSize/2)
        }

      }
    }

  }


  override def getState(): (Array[Array[Int]], Boolean) = {
    //    val state: RunnableFuture[Boolean] = new FutureTask[Boolean](new Callable[Boolean]() {
    //      override def call() {
    //
    //        gameState
    //      }
    //    })
    //    SwingUtilities.invokeLater(state)
    var state = (Array.empty[Array[Int]], gameState)
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
                state = ((boardOriginal), gameState)
      }
    })
    state
    //    ((0,0), state.get())
  }

  def nextState(state: (Array[Array[Int]]), input: Int): ((Array[Array[Int]]), Boolean) = {
    moveDirection(input, state)
    newBrick(state)
    (state, checkGameState(state))
  }
}

