package AI.MachineLearning.LinearAlgebra

class ActivationFunctions() {


  def getActivation(x: Double, typeOfActivation: String = "identity"): Double = {
    typeOfActivation match {
      case "identity" => x
      case "binary" => {
        if (x < 0) {
          0
        } else {
          1
        }
      }
      case "sigmoid" => {
        1.0 / (1.0 + Math.exp(-x))
      }
      case "arctan" => {
        (Math.exp(x) - Math.exp(-x))/(Math.exp(x) + Math.exp(-x))
      }
      case "relu" => {
        if (x < 0) {
          0
        } else {
          x
        }
      }
    }
  }
}
