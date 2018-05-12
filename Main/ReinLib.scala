package Main



import java.awt.image.BufferedImage

import Environments._
import Painting.Panel
/**
  * Created by mattias on 2017-06-27.
  */
class ReinLib(val mode: String) {

  val window = new Panel(800, 800)

  var env: GameEnvironment = null

  mode.toLowerCase match {
    case "mountaincar" => {
      env =new MountainCar(window)
    }
    case "invertedpendulum" => {
      env = new InvertedPendulum(window)
    }
    case "snake" => {
      env = new Snake(window)
    }
    case "tetris" => {
      env = new Tetris(window)
    }
    case "rushhour" => {
      env = new RushHour(window)
    }
    case "gameoflife" => {
      env = new GameOfLife(window)
    }
  }
  println("potatis")
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
}
