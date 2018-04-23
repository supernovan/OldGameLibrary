import java.net.Socket
import java.util.{Base64, Random}
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io._

import Main.ReinLib

object ConnectRasp {
  def main(args: Array[String]): Unit = {
    val gym = new ReinLib("snake")
    val rand = new Random()
    val socket = new Socket("localhost", 3333)
    val in = new DataInputStream(new BufferedInputStream(socket.getInputStream))
    val out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream))
    while (true) {
      val time = System.currentTimeMillis()

      gym.timeFrame(rand.nextInt(3) - 1)


      val img = gym.getFrame()
      val baos = new ByteArrayOutputStream()
      ImageIO.write(img, "PNG", baos)
      baos.flush()
      val imgByte = baos.toByteArray
      val base64Test = Base64.getEncoder.encodeToString(imgByte)
      baos.close()

      out.writeChars(base64Test + ";")
      out.flush()
      val sleeptime = if (33 - System.currentTimeMillis() + time < 0) 0 else 33 - System.currentTimeMillis() + time
      Thread.sleep(sleeptime)
      //println(base64Test)
    }
  }
}
