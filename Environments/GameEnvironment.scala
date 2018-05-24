package Environments

import java.awt.image.BufferedImage
import javax.swing.SwingUtilities

import Painting.Panel

import scala.collection.mutable.ArrayBuffer

trait GameEnvironment {
  type A <: Any

  /**
    * Initiates the board game
    */
  def init(): Unit

  /**
    * Takes an input Int, moves a time frame and play out the move Input
    * @param input, input which move you want to play out
    */
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
  def getInputSpace(state: A): ArrayBuffer[Int]

}
