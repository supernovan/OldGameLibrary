import Environments.GameEnvironment
import AbstractionLayer.ReinLib

object MonteCarloSearch {
  def main(args: Array[String]): Unit = {
    val gym = new ReinLib("2048")
    val inputSpace = gym.getInputSpace()
    val rand = scala.util.Random
    while (gym.getState()._2) {
      val expectedScore = Array.ofDim[(Int, Int)](inputSpace.length)
      for (i <- 0 until expectedScore.length) {
        expectedScore(i) = (0, 0)
      }
      for (simulationIterations <- 0 until 450) {
        val stateCopy: Array[Array[Int]] = gym.getState()._1.asInstanceOf[Array[Array[Int]]]
        val state = Array.ofDim[Int](4, 4)
        for (i <- 0 until 4) {
          for (j <- 0 until 4) {
            state(i)(j) = stateCopy(i)(j)
          }
        }
        var gameState = true
        var firstStep = true
        var move = 0

        do {
          val input = inputSpace(rand.nextInt(inputSpace.length))
          if (firstStep) {
            move = input
            firstStep = false
          }
          gameState = gym.simulateStep(state.asInstanceOf[gym.env.A], input)
        } while (gameState)

        //evaluate score
        val finalState = state.asInstanceOf[Array[Array[Int]]]
        var sum = 0
        for (i <- 0 until finalState.length) {
          for (j <- 0 until finalState.length) {
            while (finalState(i)(j) > 3) {
              sum += finalState(i)(j)
              finalState(i)(j) /= 2
            }
          }
        }
//        println("move : " + move + " length: " + inputSpace.length)
        expectedScore(move-1) = (expectedScore(move-1)._1+1, expectedScore(move-1)._2+sum)

      }

      var bestMove = -1
      var highestScore = -1
      for (i <- 0 until inputSpace.length) {
        val currentMove = expectedScore(i)
        if (currentMove._1 > 0) {
          if (currentMove._2 / currentMove._1 > highestScore) {
            bestMove = i+1
            highestScore = currentMove._2 / currentMove._1
          }
        }
      }

      gym.timeFrame(bestMove)

    }

  }
}
