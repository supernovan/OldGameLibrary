package Testing

import Painting.Panel
import Environments.Tetris


object TetrisTest {
  def main (args: Array[String]): Unit = {
    val panel = new Panel(800, 800)
    val env = new Tetris(panel)

    env.TestMove()
  }
}
