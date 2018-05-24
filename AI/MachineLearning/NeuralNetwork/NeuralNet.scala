package AI.MachineLearning.NeuralNetwork

import AI.MachineLearning.LinearAlgebra.ActivationFunctions

import scala.collection.mutable.ArrayBuffer

class NeuralNet {
  val inputs = ArrayBuffer.empty[Neuron]
  val layers = ArrayBuffer.empty[ArrayBuffer[Neuron]]
  val outputs = ArrayBuffer.empty[Neuron]
  val activationFunctions = new ActivationFunctions()
  val rand = new scala.util.Random()
  def addInputLayer(neuronSize: Int, activation: String = ""): Unit = {
    for (i <- 0 to neuronSize) {
      if (i == 0) {
        inputs += new Neuron(activation)
      } else {
        inputs += new Neuron(activation)
      }
    }
  }

  def addOutputLayer(neuronSize: Int, activation: String = ""): Unit = {
    for (i <- 0 until neuronSize) {
      outputs += new Neuron(activation, rand.nextDouble())
    }
  }

  def addLayer(neuronSize: Int, activation: String = "", recurrent: Boolean = false): Unit = {
    layers += ArrayBuffer.empty[Neuron]
    for (i <- 0 until neuronSize) {
      layers(layers.length-1) += new Neuron(activation, rand.nextDouble(), recurrent)
    }
  }


}
