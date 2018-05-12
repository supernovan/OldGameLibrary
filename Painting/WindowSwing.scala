package Painting


import java.awt.{Shape, Graphics, Graphics2D, EventQueue, Color}
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.awt.geom._
//import java.awt._
import java.awt.image.BufferedImage
import java.util.concurrent.Semaphore
import javax.swing.{JFrame, JPanel}

import Environments.{GameEnvironment, MountainCar}

import scala.collection.mutable.ArrayBuffer
import Main.ReinLib

object WindowSwing {
  def main(args: Array[String]): Unit = {

    val gym: ReinLib = new ReinLib("gameoflife")

    var mouseMode = false
    while (true) {

      if (mouseMode) {
        val input = gym.getMouseInput()
        gym.releaseLock()
        gym.mouseModeInput(input)
        val x = input._1 / 50
        val y = input._2 / 50
        if (x == 0 && y == 0) {
          mouseMode = false
        }
      } else {
        val input = gym.getInput()
        gym.releaseLock()
        //println(input + " " + KeyEvent.VK_RIGHT)

        if (input == KeyEvent.VK_UP) {
          gym.timeFrame(1)
        } else if (input == KeyEvent.VK_RIGHT) {
          gym.timeFrame(2)
        } else if (input == KeyEvent.VK_DOWN) {
          gym.timeFrame(3)
        } else if (input == KeyEvent.VK_LEFT) {
          gym.timeFrame(4)
        } else if (input == KeyEvent.VK_D) {
          gym.timeFrame('d')
          mouseMode = !mouseMode
        } else if (input == KeyEvent.VK_P) {
          println("choklad")
          gym.timeFrame('p')
        } else if (input == KeyEvent.VK_R) {
          gym.timeFrame('r')
        } else if (input == KeyEvent.VK_L) {
          gym.timeFrame('l')
        } else if (input == KeyEvent.VK_M) {
          gym.timeFrame('m')
        } else if (input == KeyEvent.VK_SPACE) {
          gym.timeFrame(' ')
        }
      }


      val temp = gym.getState()

      println(temp)
      println("")


    }

  //    val t1 = new Thread() {
  //      override def run(): Unit = {
  //        while (true) {
  //
  //          if (mouseMode) {
  //            val input = gym.getMouseInput()
  //            gym.releaseLock()
  //            gym.mouseModeInput(input)
  //            val x = input._1 / 50
  //            val y = input._2 / 50
  //            if (x == 0 && y == 0) {
  //              mouseMode = false
  //            }
  //          } else {
  //            val input = gym.getInput()
  //            gym.releaseLock()
  //            //println(input + " " + KeyEvent.VK_RIGHT)
  //
  //            if (input == KeyEvent.VK_UP) {
  //              gym.timeFrame(1)
  //            } else if (input == KeyEvent.VK_RIGHT) {
  //              gym.timeFrame(2)
  //            } else if (input == KeyEvent.VK_DOWN) {
  //              gym.timeFrame(3)
  //            } else if (input == KeyEvent.VK_LEFT) {
  //              gym.timeFrame(4)
  //            } else if (input == KeyEvent.VK_D) {
  //              gym.timeFrame('d')
  //              mouseMode = !mouseMode
  //            } else if (input == KeyEvent.VK_P) {
  //              println("choklad")
  //              gym.timeFrame('p')
  //            } else if (input == KeyEvent.VK_R) {
  //              gym.timeFrame('r')
  //            } else if (input == KeyEvent.VK_L) {
  //              gym.timeFrame('l')
  //            } else if (input == KeyEvent.VK_M) {
  //              gym.timeFrame('m')
  //            } else if (input == KeyEvent.VK_SPACE) {
  //              gym.timeFrame(' ')
  //            }
  //          }
  //
  //
  //          val temp = gym.getState()
  //
  //          println(temp)
  //          println("")
  //
  //
  //        }
  //      }
  //    }
  //
  //    t1.start()
  }
}



class Panel(x: Int, y: Int) extends JPanel {

  val lines: ArrayBuffer[Shape] = ArrayBuffer.empty[Shape]


  //Shapes suchs as squares
  val shapes: ArrayBuffer[Shape] = ArrayBuffer.empty[Shape]
  val shapeColors: ArrayBuffer[java.awt.Color] = ArrayBuffer.empty[java.awt.Color]

  //circles and whatnot
  val circles: ArrayBuffer[Shape] = ArrayBuffer.empty[Shape]
  val circleChoords = ArrayBuffer.empty[(Int, Int)]
  val circleColors = ArrayBuffer.empty[java.awt.Color]
  val frame = new JFrame("Rectangles")
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.add(this)
  frame.setSize(x, y)
  frame.setLocationRelativeTo(null)
  frame.setVisible(true)
  frame.setFocusable(true)
  //  frame.requestFocusInWindow()
  this.grabFocus()

  val lock = new Semaphore(2)
//  lock.acquire()


  var key: Int = 0
  var mouseKey: (Int, Int) = (0, 0)
  var keyLock = false
  var currentCoords = (0, 0)

  // Lines


  this.addKeyListener(new KeyAdapter() {
    override def keyPressed(e: KeyEvent): Unit = {

      key = e.getKeyCode
      lock.release(1)
    }

  })

  this.addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent): Unit = {
      println("x: " + e.getX + " y: " + e.getY)
      mouseKey = (e.getX, e.getY)
      lock.release(1)
    }

  })

  def waitForKeyInput(): Int = {

    lock.acquire(2)
    key
  }

  def waitForMouseClick(): (Int, Int) = {
    lock.acquire(2)
    mouseKey
  }

  def releaseOne(): Unit = {
    lock.release()
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    val g2 = g.asInstanceOf[Graphics2D]

    g2.setColor(java.awt.Color.BLACK)

    for (i <- 0 until lines.length) {
      g2.draw(lines(i))
    }

    for (i <- 0 until shapes.length) {
      g2.setColor(shapeColors(i))
      g2.draw(shapes(i))
      g2.fill(shapes(i))
    }


    for (i <- 0 until circles.length) {
      g2.setColor(circleColors(i))
      g2.draw(circles(i))
      g2.fill(circles(i))
    }




  }

  def threadSafeUpdateWindow(): Unit = {
    EventQueue.invokeLater(new Runnable {
      override def run(): Unit = updatePaint()
    })
  }

  def threadSafeCreateRectangle(x1: Double, y1: Double, x2: Double, y2: Double, h: Int, c: Color = Color.BLACK): Unit = {
    EventQueue.invokeLater(new Runnable {
      override def run(): Unit = {
        val angle = Math.atan2(y2-y1, x2-x1)

        val x3 = x1 + h* Math.sin(angle)
        val y3 = y1 - h*Math.cos(angle)
        val x4 = x2 + h* Math.sin(angle)
        val y4 = y2 - h*Math.cos(angle)

        val test = new Path2D.Double()

        test.moveTo(x1,y1)
        test.lineTo(x2,y2)
        test.lineTo(x4,y4)
        test.lineTo(x3,y3)
        test.lineTo(x1,y1)

        shapes += test
        shapeColors += c
      }
    })

  }

  override def isFocusTraversable: Boolean = true


  def lineTo(x: Int, y: Int): Unit = {
    val yourShape = new Line2D.Double(currentCoords._1, currentCoords._2, x, y)
    currentCoords = (x, y)
    lines += yourShape
    //frame.repaint()
  }

  def updatePaint(): Unit = {
    frame.repaint()
  }

  def moveTo(x: Int, y: Int): Unit = {
    currentCoords = (x, y)
  }

  def erase(): Unit = {
    resetWindow()

  }

  def getFrame(): BufferedImage = {
    val image = new BufferedImage(getWidth, getHeight, BufferedImage.TYPE_INT_RGB)
    val g2 = image.createGraphics()
    frame.paint(g2)
    image
  }



  def middleCircle(x: Int, y: Int, r: Int, c: Color = Color.BLACK): Unit = {

    val test = new Path2D.Double()



    test.moveTo(x + Math.cos(0), y - Math.sin(0))
    for (i <- 0 to 360) {
      test.lineTo(x + r*Math.cos(Math.toRadians(i)), y - r*Math.sin(Math.toRadians(i)))
    }

    circleColors += c
    circleChoords += ((x, y))
    circles += test
  }

  def getCircleChoords(): (ArrayBuffer[(Int, Int)]) = {
    circleChoords
  }






  def createRectangle(x1: Double, y1: Double, x2: Double, y2: Double, h: Int, c: Color = Color.BLACK): Unit = {
    val angle = Math.atan2(y2-y1, x2-x1)

    val x3 = x1 + h* Math.sin(angle)
    val y3 = y1 - h*Math.cos(angle)
    val x4 = x2 + h* Math.sin(angle)
    val y4 = y2 - h*Math.cos(angle)

    val test = new Path2D.Double()

    test.moveTo(x1,y1)
    test.lineTo(x2,y2)
    test.lineTo(x4,y4)
    test.lineTo(x3,y3)
    test.lineTo(x1,y1)

    shapes += test
    shapeColors += c

  }


  def drawLine(x1: Int, y1: Int, x2: Int, y2: Int): Unit = {
    val line = new Line2D.Double(x1, y1, x2, y2)

    shapes += line
  }

  def drawCurve(x1: Int, y1: Int, x2: Int, y2: Int, x3: Int, y3: Int): Unit = {
    val curve = new QuadCurve2D.Double(x1,y1,x2,y2,x3,y3)
    shapes += curve
  }




  def resetWindow(): Unit = {

    circles.clear()
    circleChoords.clear()
    circleColors.clear()

    shapes.clear()
    shapeColors.clear()

    lines.clear()
    //angles.clear()
    //shapes.clear()
  }


}

object SimpleWindow {
  def delay(ms: Int): Unit = {
    Thread.sleep(ms)
  }
}