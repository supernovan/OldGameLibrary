package Environments


import Painting.Panel
import java.awt.image.BufferedImage

import scala.collection.mutable.ArrayBuffer
import Array._
import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}
import java.util.concurrent.{Callable, FutureTask}
import javax.swing.{SwingUtilities, Timer}

import scala.util.Random

class Tetris(window: Panel) extends GameEnvironment {
  type A = (Array[(Int, Int)], ArrayBuffer[(Int, Int)])


  /*

  1 : XXXX

  2 : XX
      XX

  3 : XX
       XX

  4 :  XX
      XX

  5 : XXX
        X

  6 : XXX
      X

  7 : XXX
       X
   */

  val rotationShapes: Array[Array[Array[(Int, Int)]]] = Array(
    Array( // 1
      Array(
        (-1, 0), (0, 0), (1, 0), (2, 0)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (0, 2)
      ),
      Array(
        (-1, 0), (0, 0), (1, 0), (2, 0)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (0, 2)
      )
    ),
    Array( // 2
      Array((0, 0), (1, 0), (0, 1), (1, 1)),
      Array((0, 0), (1, 0), (0, 1), (1, 1)),
      Array((0, 0), (1, 0), (0, 1), (1, 1)),
      Array((0, 0), (1, 0), (0, 1), (1, 1))
    ),
    Array( // 3
      Array(
        (-1, 0), (0,0), (0, 1), (1, 1)
      ),
      Array(
        (-1, 0), (0,0), (0, -1), (-1, 1)
      ),
      Array(
        (-1, 0), (0,0), (0, 1), (1, 1)
      ),
      Array(
        (-1, 0), (0,0), (0, -1), (-1, 1)
      )
    ),
    Array( // 4
      Array(
        (0, 0), (1, 0), (-1, 1), (0, 1)
      ),
      Array(
        (0, 0), (0, -1), (1, 0), (1, 1)
      ),
      Array(
        (0, 0), (1, 0), (-1, 1), (0, 1)
      ),
      Array(
        (0, 0), (0, -1), (1, 0), (1, 1)
      ),


    ),

    Array( // 5
      Array(
        (-1, 0), (0, 0), (1, 0), (1, 1)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (-1, 1)
      ),
      Array(
        (-1, 0), (0, 0), (1, 0), (-1, -1)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (1, -1)
      ),


    ),

    Array( // 6
      Array(
        (-1, 0), (0, 0), (1, 0), (-1, 1)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (-1, -1)
      ),
      Array(
        (-1, 0), (0, 0), (1, 0), (1, -1)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (1, 1)
      ),
    ),
    Array( // 7
      Array(
        (-1, 0), (0, 0), (1, 0), (0, 1)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (-1, 0)
      ),
      Array(
        (-1, 0), (0, 0), (1, 0), (0, -1)
      ),
      Array(
        (0, -1), (0, 0), (0, 1), (1, 0)
      )
    )
  )


  val colorBlocks = Array(
    Color.CYAN,
    Color.YELLOW,
    Color.RED,
    Color.GREEN,
    Color.BLUE,
    Color.orange,
    new Color(186, 85, 211)
  )

  val typesOfBlocks = Array(
    Array((-1, 0), (0, 0), (1, 0), (2, 0)),     // 1
    Array((0, 0), (1, 0), (0, 1), (1, 1)),      // 2
    Array((-1, 0), (0,0), (0, 1), (1, 1)),      // 3
    Array((0, 0), (1, 0), (-1, 1), (0, 1)),     // 4
    Array((-1, 0), (0, 0), (1, 0), (1, 1)),     // 5
    Array((-1, 0), (0, 0), (1, 0), (-1, 1)),    // 6
    Array((-1, 0), (0, 0), (1, 0), (0, 1))      // 7
  )    // 6

  val deadBlocks = ArrayBuffer.empty[(Int, Int)]
  val colorDeadBlocks = ArrayBuffer.empty[Color]
  val lineCounter = ArrayBuffer.fill(16)(0)
  val blockArray = ArrayBuffer.empty[Int]
  var currentBlock = -1
  var currentShape = 0
  var choords = ((4, 3))
  var debug = false
  var paused = false
  var gameState = false
  val events = new EventQueue
//  val t1 = new Thread {
//    override def run: Unit = {
//      Thread.sleep(300)
//      while (!isInterrupted) {
//        val time = System.currentTimeMillis()
//        nextTimeState()
//        val sleepTime = 300 - (System.currentTimeMillis() - time)
//        if (sleepTime > 0) {
//         Thread.sleep(sleepTime)
//        }
//      }
//    }
//  }


  val fallDownTimer = new Timer(500, new ActionListener() {
    override def actionPerformed(e: ActionEvent): Unit = {
      events.add(5)
    }



  })

  val GameAnimationLoop = new Timer(1000/60, new ActionListener() {
    def actionPerformed(e: ActionEvent): Unit = {

      val event = events.pop()

      if (event != -1) {
        event match {
          case 1 =>
            currentShape += 1
          case 2 =>
            choords = (choords._1 + 1, choords._2)
          case 3 =>
            currentShape -= 1
          case 4 =>
            choords = (choords._1 - 1, choords._2)
          case 5 =>
            choords = (choords._1, choords._2 + 1)
          case 6 => {
            do {
              choords = (choords._1, choords._2 + 1)
            } while (validMove(5))
          }
        }

        if (currentShape > 3) {
          currentShape = 0
        } else if (currentShape < 0) {
          currentShape = 3
        }
        validMove(event)

        reset()
        paintGrid()
        paintBlock()
        window.repaint()

      }
    }
  })
  /*
    DIRECTION

        1
      4   2
        3
   */

  def init(): Unit = {
    paintGrid()
    initBlocks()
    fillGrid()
    window.updatePaint()
    GameAnimationLoop.start()
    fallDownTimer.start()
  }

  private def initBlocks(): Unit = {

    currentBlock = Random.nextInt(7)
    for (i <- 0 until 5) {
      blockArray += Random.nextInt(6)
    }

  }

  private def rotateBlock(): Unit = {

  }

  private def findEmptyTile(): (Int, Int) = {
    val rand = new Random()

    var x = rand.nextInt(10)
    var y = rand.nextInt(10)



    ((x, y))
  }

  private def moveDownRows(y: Int): Unit = {
    for (i <- 0 until deadBlocks.length) {
      val currV = deadBlocks(i)._2
      if (currV <= y) {
       deadBlocks(i)  = ((deadBlocks(i)._1, currV+1))
      }
    }

  }

  private def deleteRow(y: Int) {
    var index = 0
    while (index < deadBlocks.length) {
      if (deadBlocks(index)._2 == y) {
        deadBlocks.remove(index)
        colorDeadBlocks.remove(index)
      }  else {
        index += 1
      }
    }
  }

  private def nextTimeState(): Unit = {
    if (!false) {
      synchronized {
        if (nextBlockSafe()) {
          choords = ((choords._1, choords._2 + 1))
        } else {
          for (i <- rotationShapes(currentBlock)(currentShape)) {
            if (i._2 + choords._2 <= 1 && i._1 + choords._1 == 4) {
              //lost the game
            }
            deadBlocks += ((i._1 + choords._1, i._2 + choords._2))
            colorDeadBlocks += colorBlocks(currentBlock)

            lineCounter(i._2 + choords._2 - 1) = lineCounter(i._2 + choords._2 - 1) + 1
            if (lineCounter(i._2 + choords._2 - 1) >= 10) {

              lineCounter(i._2 + choords._2 - 1) = 0
              deleteRow(i._2 + choords._2)
              moveDownRows(i._2 + choords._2)
            }
          }

          currentShape = 0
          currentBlock = blockArray.remove(0)
          blockArray += Random.nextInt(7)
          choords = (4, 1)
        }

        reset()
        paintGrid()
        paintBlock()
        window.repaint()
      }
    }
  }

  private def nextBlockSafe(): Boolean = {
    var flag = true

    for (i <- rotationShapes(currentBlock)(currentShape) if flag) {
      val currX = i._1 + choords._1
      val nextY = i._2 + choords._2 + 1
      if (deadBlocks.contains((currX, nextY))) {
        flag = false
      } else if (nextY > 16) {
        flag = false
      }
    }

    flag
  }

  private def fillGrid(): Unit = {

    val leftX = 101
    println(typesOfBlocks.length)
    for (i <- typesOfBlocks(currentBlock)) {
      val x1 = leftX+(i._1+choords._1)*50
      val x2 = x1 + 48
      val y1 = (i._2+choords._2)*50 - 1
      val y2 = y1
      window.threadSafeCreateRectangle(x1, y1, x2, y2, 48, colorBlocks(currentBlock))
    }






  }





  //private def

  private def checkCurrentBlock(): Boolean = {
    var flag = true

    for (i <- rotationShapes(currentBlock)(currentShape) if flag) {
      val currX = i._1 + choords._1
      val currY = i._2 + choords._2
      if (deadBlocks.contains((currX, currY))) {
        flag = false
      } else if (currY > 16 || currX < 0 || currX > 9) {
        flag = false
      }
    }

    flag
  }

  private def validMove(input: Int): Boolean = {
    var flag = true
    if (!checkCurrentBlock()) {
      flag = false
      input match {
        case 1 => currentShape -= 1
        case 2 => choords = (choords._1 -1, choords._2)
        case 3 =>  currentShape += 1
        case 4 => choords = (choords._1 + 1, choords._2)
        case 5 => {
          choords = (choords._1, choords._2-1)
          for (i <- rotationShapes(currentBlock)(currentShape)) {
            if (i._2 + choords._2 <= 1 && i._1 + choords._1 == 4) {
              System.exit(0)
            }
            deadBlocks += ((i._1 + choords._1, i._2 + choords._2))
            colorDeadBlocks += colorBlocks(currentBlock)

            lineCounter(i._2 + choords._2 - 1) = lineCounter(i._2 + choords._2 - 1) + 1

          }
          for (i <- 0 until lineCounter.length) {
            if (lineCounter(i) >= 10) {
              lineCounter.remove(i)
              lineCounter.prepend(0)
              if (debug) {
                printBoard()
              }
              deleteRow(i+1)
              moveDownRows(i+1)
              if (debug) {
                printBoard()
              }
            }
          }


          currentShape = 0
          currentBlock = blockArray.remove(0)
          blockArray += Random.nextInt(7)
          choords = (4, 1)
        }
//        case 'd' => debug = true
      }
    }
    if (currentShape > 3) {
      currentShape = 0
    } else if (currentShape < 0) {
      currentShape = 3
    }
    flag
  }

  def timeFrame(input: Int): Unit = {
    SwingUtilities.invokeLater(new Runnable {
      override def run(): Unit = {
        input match {
          case 1 => events.add(1)
          case 2 => events.add(2)
          case 3 => events.add(3)
          case 4 => events.add(4)
          case ' ' => events.add(6)
          case 'p' => {
            if (paused) {
              GameAnimationLoop.start()
              fallDownTimer.start()
              paused = false
            } else {
              GameAnimationLoop.stop()
              fallDownTimer.stop()
              paused = true
            }

          }
          case 'd' => debug = !debug
          case 'r' => {
            for (i <- 0 until lineCounter.length) {
              if (lineCounter(i) >= 10) {
                deleteRow(i+1)

              }
            }
          }
          case 'm' => {
            moveDownRows(17)
          }
          case 'l' => {
            printBoard()
            }
        }
      }
    })
  }

  override def getInputSpace(): Array[Int] = Array(1, 2, 3, 4)

  def printBoard(): Unit = {
    val temp = Array.ofDim[Int](16, 10)
    for (i <- deadBlocks) {
      temp(i._2-1)(i._1) = 2
    }
    for (i <- temp) {
      for (j <- i) {
        print(j + " ")
      }
      print("\n")
    }
  }
  val TILE_SIZE = 50
  val GRID_W = 8
  val GRID_H = 18
  val START_PIXEL_X = 101

  private def paintGrid(): Unit = {

    if (debug) {
      window.moveTo(0,0)
      window.lineTo(50, 0)
      window.lineTo(50, 50)
      window.lineTo(0, 50)
      window.lineTo(0, 0)
    }


    for (x <- 100 to 600 by 50) {
      window.moveTo(x, 0)
      window.lineTo(x, 800)
    }

    for (y <- 0 to 800 by 50) {
      window.moveTo(100, y)
      window.lineTo(600, y)
    }

    for (x <- 650 to 750 by 50) {
      window.moveTo(x, 0)
      window.lineTo(x, 800)
    }

    for (y <- 0 to 800 by 50) {
      window.moveTo(650, y)
      window.lineTo(750, y)
    }

  }

  private def paintBlock(): Unit = {

    val leftX = START_PIXEL_X
    //println(typesOfBlocks.length)
    val temp = rotationShapes(currentBlock)
    for (i <- temp(currentShape)) {

      val x1 = leftX+(i._1+choords._1)*TILE_SIZE
      val x2 = x1 + TILE_SIZE - 2
      val y1 = (i._2+choords._2)*TILE_SIZE - 1
      val y2 = y1
      window.threadSafeCreateRectangle(x1, y1, x2, y2, TILE_SIZE-2, colorBlocks(currentBlock))
    }

    for (x <- 0 until deadBlocks.length) {
      val i = deadBlocks(x)
      val x1 = leftX+(i._1)*TILE_SIZE
      val x2 = x1 + TILE_SIZE - 2
      val y1 = (i._2)*TILE_SIZE - 1
      val y2 = y1
      window.threadSafeCreateRectangle(x1, y1, x2, y2, TILE_SIZE-2, colorDeadBlocks(x))
    }
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
    val x = input._1 / TILE_SIZE - 2
    val y = input._2 / TILE_SIZE +1

    if (x == -2 && y == 1) {
      debug = false
    } else {
      if (!deadBlocks.contains(x, y)) {
        deadBlocks += ((x, y))
        colorDeadBlocks += Color.DARK_GRAY
        lineCounter(y-1) += 1
        reset()
        paintGrid()
        paintBlock()
        window.repaint()
      }
    }





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


  def TestMove(): Unit = {
    for (i <- 1 to 13) {
      colorDeadBlocks += Color.BLUE
    }

    for (i <- 0 to 9) {
      deadBlocks += ((i, 16))
    }
    currentBlock = 0
    deadBlocks += ((0, 15))
    deadBlocks += ((0, 14))
    deadBlocks += ((1, 14))

    println(deadBlocks.length)

    paintGrid()
    paintBlock()
    window.threadSafeUpdateWindow()

    println("innan")
    for (i <- deadBlocks) {
      print(i + " ")
    }
    println(" ta bort rad 16")

    deleteRow(16)
    for (i <- deadBlocks) {
      println(i)
    }

    println("flyta raden")
    moveDownRows(15)
    for (i <- deadBlocks) {
      println(i)
    }


    println("banan")




  }

  override def getState(): ((Array[(Int, Int)], ArrayBuffer[(Int, Int)]), Boolean) = {
//    val state = new FutureTask[((Array[(Int, Int)], ArrayBuffer[(Int, Int)]), Boolean)](new Callable[((Array[(Int, Int)], ArrayBuffer[(Int, Int)]), Boolean)]() {
//      override def call() {
//        ((rotationShapes(currentBlock)(currentBlock), deadBlocks), gameState)
//      }
//    })
//    SwingUtilities.invokeLater(state)
//    state.get()
    var state: ((Array[(Int, Int)], ArrayBuffer[(Int, Int)]), Boolean) = null
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit = {
        state = ((rotationShapes(currentBlock)(currentShape), deadBlocks), gameState)
      }
    })
    state
  }

  def nextState(state: (Array[(Int, Int)], ArrayBuffer[(Int, Int)]), input: Int): ((Array[(Int, Int)], ArrayBuffer[(Int, Int)]), Boolean) = {


    (state, gameState)
  }

  class EventQueue() {
    val events = ArrayBuffer.empty[Int]

    def pop(): Int = {
      if (empty()) -1 else events.remove(0)
    }

    def add(event: Int): Unit = {
      events += event
    }

    def empty(): Boolean = {
      events.isEmpty
    }
  }
}

