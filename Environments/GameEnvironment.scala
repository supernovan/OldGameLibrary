package Environments

import java.awt.image.BufferedImage
import javax.swing.SwingUtilities

import Painting.Panel

trait GameEnvironment {
  type A <: Any
  def init(): Unit
  def timeFrame(input: Int): Unit
  def mouseModeInput(input: (Int, Int)): Unit
  def reset(): Unit
  def getInput(): Int
  def getMouseInput(): (Int, Int)
  def getFrame(): BufferedImage
  def releaseLock(): Unit
  def updatePaint(): Unit
  def getState(): (Any, Boolean)
  def nextState(state: A, input: Int): (A, Boolean)
  def getInputSpace(): Array[Int]

}
