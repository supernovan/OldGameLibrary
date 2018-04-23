package tetris



import java.awt.event.KeyEvent
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.event.EventHandler
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage


import scala.collection.mutable.ArrayBuffer

object TetrisApp {
  val TILE_SIZE = 40
  val GRID_WIDTH = 15
  val GRID_HEIGHT = 20


  def main(args: Array[String]) {
    Application.launch(classOf[TetrisApp], args: _*)
  }
}



class TetrisApp extends Application {


  var time: Double = 0
  var g: GraphicsContext = null

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
    Color.ORANGE,
//    new Color(186, 85, 211)
    Color.PURPLE
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


  val gridW = TetrisApp.GRID_WIDTH
  val gridH = TetrisApp.GRID_HEIGHT
  val gridT = TetrisApp.TILE_SIZE
//  val deadBlocks = Array.empty[Array]
//  val deadBlocks = ArrayBuffer.empty[(Int, Int)]
  val deadBlocks = Array.ofDim[Int] (gridW, gridH)

  val rowCounter = Array.ofDim[Int](gridH)
  val colCounter = Array.ofDim[Int](gridW)
  val colorDeadBlocks = ArrayBuffer.empty[Color]
//  val lineCounter = ArrayBuffer.fill(16)(0)
  val blockArray = ArrayBuffer.empty[Int]
  var currentBlock = -1
  var currentShape = 0
  var choords = ((3, 2))
  val maxX = 8
  val maxY = 19




  override def start(primaryStage: Stage): Unit = {
    val scene = new Scene(createContent())
    import com.sun.javafx.scene.traversal.Direction
    import javafx.scene.input.KeyCode

    currentBlock = 6


    scene.setOnKeyPressed((e) => {


      e.getCode match {
        case KeyCode.SPACE => println("PRessed Space")
        case KeyCode.UP => {
          if (checkMove(1)) {
            currentShape = (currentShape + 1)% 4
          }
//
        }
        case KeyCode.LEFT => {
          if (checkMove(4)) {
            choords = (choords._1 - 1, choords._2)

          }
//          println(System.currentTimeMillis()-time)
        }
        case KeyCode.RIGHT => {
          if (checkMove(2)) {
            choords = (choords._1 + 1, choords._2)
          }
//          println(System.currentTimeMillis()-time)
        }
        case KeyCode.DOWN => {
          if (checkMove(3)) {
            currentShape = if (currentShape-1 < 0) 3 else currentShape - 1

          }
//          println(System.currentTimeMillis()-time)
        }
        case KeyCode.G => {
          System.gc()
          println("garbage")
        }

        case _ =>


      }
      val time = System.currentTimeMillis()
      render()
      println(System.currentTimeMillis()-time)
    })

    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def createContent(): Parent = {
    val root = new Pane()


    root.setPrefSize(gridW*gridT, gridH*gridT)
    val canvas = new Canvas(gridW*gridT, gridH*gridT)
    g = canvas.getGraphicsContext2D

    root.getChildren.addAll(canvas)
    var lastTime = System.currentTimeMillis()
    val timer: AnimationTimer = new AnimationTimer() {
      override def handle(now: Long): Unit = {
        time += 0.017

//        if (newTime - lastTime > 200) {
//          println(newTime - lastTime)
//        }
//        lastTime = newTime
        if (time >= 0.5) {
          val newTime = System.currentTimeMillis()
          timeFrame()

          render()
          println(System.currentTimeMillis() - newTime)
          time = 0
        }
      }
    }

    timer.start()


    root
  }

  def timeFrame(): Unit = {
    if (checkMove(5)) {
      choords = (choords._1, choords._2 + 1)
    } else {
//      val temp = rotationShapes(currentBlock)(currentShape)
      for (i <- rotationShapes(currentBlock)(currentShape)) {
        deadBlocks(choords._1 + i._1)(choords._2 + i._2) = currentBlock + 1
//        colorDeadBlocks += colorBlocks(currentBlock)
      }
      currentBlock = scala.util.Random.nextInt(7)
      currentShape = 0
      choords = (3, 1)
    }
  }

  def checkMove(move: Int): Boolean = {
    var flag = true

    move match {
      case 1 => {
        val rotMove = (currentShape + 1) % 4
        val temp = rotationShapes(currentBlock)(rotMove)
        flag = helpCheck(temp, choords)
      }
      case 2 => {
        val place = (choords._1 + 1, choords._2)
        val temp = rotationShapes(currentBlock)(currentShape)
        flag = helpCheck(temp, place)
      }
      case 3 => {
        val rotMove =if (currentShape-1 < 0) 3 else currentShape - 1
        val temp = rotationShapes(currentBlock)(rotMove)
        flag = helpCheck(temp, choords)
      }
      case 4 => {
        val place = (choords._1 - 1, choords._2)
        val temp = rotationShapes(currentBlock)(currentShape)
        flag = helpCheck(temp, place)
      }
      case 5 => {
        val place = (choords._1, choords._2 + 1)
        val temp = rotationShapes(currentBlock)(currentShape)
        flag = helpCheck(temp, place)
      }
      case _ =>
    }

    flag
  }

  def helpCheck(blocks: Array[(Int, Int)], place: (Int, Int)): Boolean = {
    if (blocks.forall(p => (p._1 + place._1 <= 1 || p._1 + place._1 >= maxX).|| (p._2 + place._2 >= maxY - 1))) {
      false
    } else if (blocks.forall(p => deadBlocks(p._1 + place._1)( p._2 + place._2) == 0)) {
      true
    } else {
      false
    }
  }

  def render(): Unit = {
    g.clearRect(0, 0, gridW*gridT, gridH*gridT)
    val temp: Array[(Int, Int)] = rotationShapes(currentBlock)(currentShape)

    drawGrid()

    val x = choords._1
    val y = choords._2
    val startX = 3*gridT
    val currentBlockValues = Array.ofDim[(Int, Int)](4)
    var counter = 0


    for (i <- 0 until deadBlocks.length; j <- 0 until deadBlocks(i).length) {

      val temp = deadBlocks(i)(j)
      if (temp == 0) {

      } else {
        g.setFill(colorBlocks(temp-1))
        val x1 = i * gridT + startX + 1
        val y1 = j * gridT + 1
        g.fillRect(x1, y1, gridT - 2, gridT - 2)
      }
    }

    g.setFill(colorBlocks(currentBlock))
    for (i <- temp) {
      val x1 = (x + i._1) * gridT + startX + 1
      val y1 = (y + i._2) * gridT + 1
      g.fillRect(x1, y1, gridT - 2, gridT - 2)
    }

  }

  def drawGrid(): Unit = {
    g.setFill(Color.BLACK)
    for (i <- 3 to gridW - 3) {
      g.moveTo(i*gridT, 0)
      g.lineTo(i*gridT, gridH*gridT)
    }

    for (i <- 0 to gridH) {
      g.moveTo(3*gridT , i*gridT)
      g.lineTo((gridW-3)*gridT, i*gridT)
    }
    g.stroke()
  }
}
