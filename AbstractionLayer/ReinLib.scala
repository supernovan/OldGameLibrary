package AbstractionLayer



import java.awt.image.BufferedImage

import Environments._
import Painting.Panel

import scala.collection.mutable.ArrayBuffer

/**
  * Created by mattias on 2017-06-27.
  */
class ReinLib(val mode: String) {


  val window = new Panel(800, 800)

  val env: GameEnvironment = mode match {
    case "gameoflife" => {
      new GameOfLife(window)
    }
    case "invertedpendulum" => {
      new InvertedPendulum(window)
    }
    case "mountaincar" => {
      new MountainCar(window)
    }
    case "2048" => {
      new TwentyFortyEight(window)
    }
    case "tetris" => {
      new Tetris(window)
    }
    case "snake" => {
      new Snake(window)
    }
  }






  env.init

  def timeFrame(input: Int): Unit = {
    env.timeFrame(input)
  }

  def mouseModeInput(input: (Int, Int)) = {
    env.mouseModeInput(input)
  }

  def getInput(): Int = {
    env.getInput()
  }

  def reset(): Unit = {
    env.reset()
  }

  def getMouseInput(): (Int, Int) = {
    env.getMouseInput()
  }


  def getFrame(): BufferedImage = {
    env.getFrame()
  }


  def releaseLock(): Unit = {
    env.releaseLock()
  }

  def getState(): (Any, Boolean) = {
    env.getState()
  }

  def getInputSpace(state: env.A): ArrayBuffer[Int] = {
    env.getInputSpace(state)
  }

  def simulateStep(state: env.A, input: Int): (Boolean) = {

    env.nextState(state, input)._2
  }
}
