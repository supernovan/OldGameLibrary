package Environments

import Painting.Panel
import java.awt.image.BufferedImage

import scala.collection.mutable.ArrayBuffer
import Array._
import java.awt.{Color, Toolkit}
import java.util.concurrent.{Callable, FutureTask}
import javax.swing.SwingUtilities

import scala.util.Random

class Snake(window: Panel) extends GameEnvironment {
  type A = (ArrayBuffer[(Int, Int)], (Int, Int), Int)

  val grid = ofDim[Int](10, 10)

  val SNAKE = -1
  val SNAKE_HEAD = -2
  val EMPTY = 0
  val APPLE = 1

  var gameState = true
  var direction = 3

  val snakeBody = ArrayBuffer.empty[(Int, Int)]
  var applePos = (-1, -1)
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
        initSnakeAndApple()
        //fillGrid()
        window.updatePaint()
      }
    })

  }

  private def initSnakeAndApple(): Unit = {
    snakeBody += ((1,1))
    snakeBody += ((1,2))
    snakeBody += ((1,3))

    for (i <- 0 until snakeBody.length) {
      val x1 = 151+snakeBody(i)._1*50
      val x2 = x1 + 48
      val y1 = 199+snakeBody(i)._2*50
      val y2 = y1
      if (i != snakeBody.length - 1) {
        window.createRectangle(x1, y1, x2, y2, 48, Color.YELLOW)
      } else {
        window.createRectangle(x1, y1, x2, y2, 48, Color.BLUE)
      }
    }

    applePos = findEmptyTile()
    val x1 = 151+applePos._1*50
    val x2 = x1 + 48
    val y1 = 199+applePos._2*50
    val y2 = y1
    window.createRectangle(x1, y1, x2, y2, 48, Color.RED)



  }
  // Please rewrite this one
  private def findEmptyTile(): (Int, Int) = {
    val rand = new Random()

    var x = rand.nextInt(10)
    var y = rand.nextInt(10)

    while (snakeBody.contains((x, y)) == true && snakeBody.length < 99) {
      x = rand.nextInt(10)
      y = rand.nextInt(10)
    }

    ((x, y))
  }



  private def fillGrid(): Unit = {
    for (i <- 0 until snakeBody.length) {
      val x1 = 151+snakeBody(i)._1*50
      val x2 = x1 + 48
      val y1 = 199+snakeBody(i)._2*50
      val y2 = y1
      if (i != snakeBody.length - 1) {
        window.createRectangle(x1, y1, x2, y2, 48, Color.YELLOW)
      } else {
        window.createRectangle(x1, y1, x2, y2, 48, Color.BLUE)
      }
    }
    val x1 = 151+applePos._1*50
    val x2 = x1 + 48
    val y1 = 199+applePos._2*50
    val y2 = y1
    window.createRectangle(x1, y1, x2, y2, 48, Color.RED)
  }

  private def paintGrid(): Unit = {

    for (x <- 150 to 650 by 50) {
      window.moveTo(x, 150)
      window.lineTo(x, 650)
    }

    for (y <- 150 to 650 by 50) {
      window.moveTo(150, y)
      window.lineTo(650, y)
    }
  }

  private def vaildMove(input: Int): Boolean = {
    if (direction == input +2 || direction == input -2) {
      false
    } else {
      true
    }
  }

  def timeFrame(input: Int): Unit = {
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        if (vaildMove(input)) {
          direction = input
        }

        direction match {
          case 1 => if(!checkDeadlyState(snakeBody.last._1, snakeBody.last._2-1)) {
            snakeBody += ((snakeBody.last._1, snakeBody.last._2-1))
            if ((snakeBody.last._1, snakeBody.last._2) != applePos) {
              snakeBody.remove(0)
            } else {
              applePos = findEmptyTile()
            }
          } else {
//            System.out.println("You lost")
            //        reset()
            gameState = false
          }
          case 2 => if(!checkDeadlyState(snakeBody.last._1+1, snakeBody.last._2)) {
            snakeBody += ((snakeBody.last._1+1, snakeBody.last._2))
            if ((snakeBody.last._1, snakeBody.last._2) != applePos) {
              snakeBody.remove(0)
            } else {
              applePos = findEmptyTile()
            }
          } else {
//            System.out.println("You lost")
            //        reset()
            gameState = false
          }
          case 3 =>
//            println((snakeBody.head._1, snakeBody.head._2+1))
//            println(applePos)
            if(!checkDeadlyState(snakeBody.last._1, snakeBody.last._2+1)) {
              snakeBody += ((snakeBody.last._1, snakeBody.last._2+1))
              if ((snakeBody.last._1, snakeBody.last._2) != applePos) {
                snakeBody.remove(0)
              } else {
                applePos = findEmptyTile()
              }
            } else {
//              System.out.println("You lost")
              //        reset()
              gameState = false
            }
          case 4 => if(!checkDeadlyState(snakeBody.last._1-1, snakeBody.last._2)) {
            snakeBody += ((snakeBody.last._1-1, snakeBody.last._2))
            if ((snakeBody.last._1, snakeBody.last._2) != applePos) {
              snakeBody.remove(0)
            } else {
              applePos = findEmptyTile()
            }
          } else {
//            System.out.println("You lost")
            //        reset()
            gameState = false
          } case -1 => {

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


  private def checkDeadlyState(x: Int, y: Int): Boolean = {
    if (snakeBody.contains((x, y)) || x < 0 || x> 9 || y < 0 || y > 9) {
      true
    } else {
      false
    }
  }
  def reset(): Unit = {
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        gameState = true
        window.resetWindow()
        paintGrid()
        snakeBody.clear()
        initSnakeAndApple()
        window.updatePaint()
        direction = 3
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

  override def getState(): ((ArrayBuffer[(Int, Int)], (Int, Int), Int), Boolean) = {
//    val state = new FutureTask[((ArrayBuffer[(Int, Int)], (Int, Int)), Boolean)](new Callable[((ArrayBuffer[(Int, Int)], (Int, Int)), Boolean)]() {
//      override def call() {
//        ((snakeBody, applePos), gameState)
//      }
//    })
//    SwingUtilities.invokeLater(state)
//    state.get()
    var state: ((ArrayBuffer[(Int, Int)], (Int, Int), Int), Boolean) = null
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        state = ((snakeBody, applePos, direction), gameState)
      }
    })
    state
  }

  override def getInputSpace(): Array[Int] = Array(1,2,3,4)

  def nextState(state: (ArrayBuffer[(Int, Int)], (Int, Int), Int), input: Int): ((ArrayBuffer[(Int, Int)], (Int, Int), Int), Boolean) = {


    (state, gameState)
  }
}
