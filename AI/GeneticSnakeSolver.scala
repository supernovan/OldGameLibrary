package AI

import java.awt.event.{ActionEvent, ActionListener}
import java.io._
import java.net.Socket
import java.util.Base64
import java.net.Socket
import java.util.{Base64, Random}
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io._
import Main.ReinLib

import scala.collection.mutable.ArrayBuffer


/*
    DIRECTION

        1
      4   2
        3
   */

object GeneticSnakeSolver {

  val tournSize = 20
  val probArray = Array.ofDim[Double](tournSize)
  val probChance = 0.75
  val mutRate = 0.05

  for (i <- 0 until tournSize) {
    probArray(i) = probChance*Math.pow(1-probChance, i+1)
  }

  def main(args: Array[String]): Unit = {
    val gym: ReinLib = new ReinLib("snake")
    val popSize = 60

//    val socket = new Socket("localhost", 3333)
//    val in = new DataInputStream(new BufferedInputStream(socket.getInputStream))
//    val out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream))



    var population = ArrayBuffer.empty[NeuralNet]

    for (i <- 0 until popSize) {
      population += new NeuralNet()
    }

    while (true) {
      gym.getInput()
      for (i <- population) {
        var game = true
        var fitness = 0.1
        do {
          Thread.sleep(100)
          val img = gym.getFrame()
//          val baos = new ByteArrayOutputStream()
//          ImageIO.write(img, "PNG", baos)
//          baos.flush()
//          val imgByte = baos.toByteArray
//          val base64Test = Base64.getEncoder.encodeToString(imgByte)
//          baos.close()
//
//          out.writeChars(base64Test + ";")
//          out.flush()
          val state = gym.getState()
          game = state._2
          val temp = state._1.asInstanceOf[(ArrayBuffer[(Int, Int)], (Int, Int), Int)]
          val body = temp._1
          val headPos = body(body.length-1)
          val applePos = temp._2


          val input = Array.ofDim[Double](6)



          val direction = temp._3
          val direction2 = fixDirection(direction - 1)
          val direction3 = fixDirection(direction + 1)
          val newDir = translateDirection(direction)
          val newDir2 = translateDirection(direction2)
          val newDir3 = translateDirection(direction3)

          val distHead = manhattanDist((headPos._1, headPos._2),(applePos._1, applePos._2))
          val dist1 = manhattanDist((headPos._1 + newDir._1, headPos._2 + newDir._2),(applePos._1, applePos._2))
          val dist2 = manhattanDist((headPos._1 + newDir2._1, headPos._2 + newDir2._2),(applePos._1, applePos._2))
          val dist3 = manhattanDist((headPos._1 + newDir3._1, headPos._2 + newDir3._2),(applePos._1, applePos._2))

          var ahead = 0
          var left = 0
          var right = 0
          if ((dist1 % 1.0) < Math.pow(10, -5) && dist1 < distHead) {
            ahead = 1
            left = 0
            right = 0
          } else {
            ahead = 0
            if (dist2 > dist3) {
              right = 1
            } else {
              left = 1
            }
          }

          input(0) = checkDirection(headPos._1 + newDir._1, headPos._2 + newDir._2, body)
          input(1) = checkDirection(headPos._1 + newDir2._1, headPos._2 + newDir2._2, body)
          input(2) = checkDirection(headPos._1 + newDir3._1, headPos._2 + newDir3._2, body)
          input(3) = ahead
          input(4) = left
          input(5) = right


          var sum1 = 0.0
          var sum2 = 0.0
          var sum3 = 0.0

          for (j <- 0 until i.weights.length) {
            sum1 += input(j)*i.weights(j)(0)
            sum2 += input(j)*i.weights(j)(1)
            sum3 += input(j)*i.weights(j)(2)
          }


          if (sum1 > sum2 && sum1 > sum3) {
            gym.timeFrame(direction)
            val fit =  if (distHead > dist1) 1 else -2.5
            fitness += fit
//            println("new: " + dist1.toString + " old: " + distHead.toString + " fit: " + fit)
            if (dist1 < 0.001) fitness += 10
          } else if (sum2 > sum1 && sum2 > sum3) {
            gym.timeFrame(direction2)
            val fit =  if (distHead > dist2) 1 else -1.5
            fitness += fit
//            println("new: " + dist2.toString + " old: " + distHead.toString + " fit: " + fit)

            if (dist2 < 0.001) fitness += 10
          } else if (sum3 > sum2 && sum3 > sum1) {
            gym.timeFrame(direction3)
            val fit =  if (distHead > dist3) 1 else -1.5
            fitness += fit
//            println("new: " + dist3.toString + " old: " + distHead.toString + " fit: " + fit)

            if (dist3 < 0.001) fitness += 10
          }







//          println(fitness)
//          Thread.sleep(300)


        } while (game && fitness > 0)
        i.fitness = fitness
        gym.reset()
      }
      population = newGeneration(population)
    }
  }

  def newGeneration(currGen: ArrayBuffer[NeuralNet]): ArrayBuffer[NeuralNet] = {
    val newGen = ArrayBuffer.empty[NeuralNet]
    val sortedGen = currGen.sortBy(_.fitness)

    newGen ++= tournamentSel(sortedGen)

    for (i <- 0 until tournSize) {
      mutation(newGen(i))
    }

    for (i <- 0 until sortedGen.length - tournSize) {
      newGen += new NeuralNet
    }


    newGen
  }

  def mutation(net: NeuralNet): Unit = {
    for (i <- 0 until  net.weights.length) {
      for (j <- 0 until net.weights(i).length) {
        if (scala.util.Random.nextDouble() < mutRate) {
          net.weights(i)(j) = scala.util.Random.nextDouble()
        }
      }
    }
  }

  def tournamentSel(par: ArrayBuffer[NeuralNet]): ArrayBuffer[NeuralNet] = {
    val children = ArrayBuffer.empty[NeuralNet]
    for (i <- 0 until tournSize) {
      var counter = 0
      val tempChildren = ArrayBuffer.empty[NeuralNet]
      while (tempChildren.length < 2) {
        val prob = scala.util.Random.nextDouble()
        if (probArray(counter) > prob) {
          tempChildren += par(counter)
        }
        counter += 1
        counter %= tournSize
      }
      children += crossover(tempChildren(0), tempChildren(1))
    }
    children
  }

  def crossover(par1: NeuralNet, par2: NeuralNet): NeuralNet = {
    val child = new NeuralNet
    for (i <- 0 until 6) {
      for (j <- 0 until 3) {
        if (scala.util.Random.nextBoolean()) {
          child.weights(i)(j) = par1.weights(i)(j)
        } else {
          child.weights(i)(j) = par2.weights(i)(j)
        }
      }
    }
    child
  }

  def fixDirection(dir: Int): Int = {
    if (dir > 4) {
      1
    } else if (dir < 1) {
      4
    } else {
      dir
    }
  }

  def checkDirection(x: Int, y: Int, body: ArrayBuffer[(Int, Int)]): Double = {
    if (body.contains((x, y))) {
      0
    } else if (x < 0 || x >= 10 || y < 0 || y > 10) {
      0
    } else {
      1
    }
  }

  def translateDirection(dir: Int): (Int, Int) = {
    dir match {
      case 1 => (0, -1)
      case 2 => (1, 0)
      case 3 => (0, 1)
      case 4 => (-1, 0)
      case _ => (0, 0)
    }
  }

  def greedy(): Unit = {
    val gym: ReinLib = new ReinLib("snake")
    while (true) {
      val state = gym.getState()
      if (!state._2) {
        gym.reset()
      } else {



      }
    }
  }

  def shortestPath(pos1: (Int, Int), pos2: (Int, Int)): Int = {
    0
  }

  def manhattanDist(pos1: (Int, Int), pos2: (Int, Int)): Int = {
    Math.abs(pos1._1 - pos2._1) + Math.abs(pos1._2 - pos2._2)
  }

  class NeuralNet() {
    val weights = Array.ofDim[Double](6, 3)

    var fitness = 0.0

    for (i <- 0 until weights.length) {
      for (j <- 0 until weights(i).length) {
        weights(i)(j) = scala.util.Random.nextDouble()
      }
    }
  }
}
