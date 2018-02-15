/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmodel

import java.io.{DataInput, DataInputStream, IOException, InputStream}
import java.nio.ByteBuffer

import scala.annotation.tailrec
import scala.util.Try

import org.apache.commons.io.IOUtils

import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.helper.LogHelper
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class DanModelDescription(name: String, description: String, author: String)
case class DanModelReadException(message: String) extends IOException(message)
object DanModelReader {

  def createForm(resource: ResourceLocation, name: String): Form =
    new FormDanModel(name, resource)

  @SideOnly(Side.CLIENT)
  def readModel(resource: ResourceLocation): Try[(DanModelDescription, DanModel)] = {
    var rawIs: InputStream = null

    val res = Try {
      rawIs = Minecraft.getMinecraft.getResourceManager
        .getResource(new ResourceLocation(resource + ".danmodel"))
        .getInputStream

      if (rawIs == null) throw new IllegalArgumentException("No model at that location")
      val is: DataInput = new DataInputStream(rawIs)

      val danModel = readString(is)
      if (danModel != "DanmakuCore DanModel") throw DanModelReadException("Not a DanModel file")
      val version = is.readShort()
      if (version != 1) throw DanModelReadException(s"Unknown version $version")

      val name        = readString(is)
      val description = readString(is)
      val author      = readString(is)

      val modelDescription = DanModelDescription(name, description, author)

      val pieces   = is.readInt()
      val danAlpha = is.readFloat()

      val modelLength = is.readInt()
      val data        = new Array[Byte](modelLength)
      is.readFully(data)

      LogHelper.trace(s"Verifying danmodel $name found at $resource")
      verifyData(data, pieces)

      val model = new DanModel(data, pieces, danAlpha)

      (modelDescription, model)
    }

    if (rawIs != null) IOUtils.closeQuietly(rawIs)

    res
  }

  private def readString(is: DataInput): String = {
    val array = new Array[Byte](is.readShort())
    is.readFully(array)
    new String(array, "UTF-8")
  }

  private def verifyData(data: Array[Byte], pieces: Int): Unit = {
    val buf = ByteBuffer.wrap(data)

    @tailrec
    def verifyPiece(i: Int): Unit = {
      if (i < pieces) {
        LogHelper.trace(s"Verifying piece $i")
        LogHelper.trace(s"GLMode: ${buf.getInt()}")
        val vertices    = buf.getInt()
        val useDanColor = buf.get() != 0
        LogHelper.trace(s"Vertices: $vertices")
        LogHelper.trace(s"UseDanColor: $useDanColor")

        @tailrec
        def verifyVertex(j: Int): Unit = {
          if (j < vertices) {
            buf.getDouble() //x
            buf.getDouble() //y
            buf.getDouble() //z
            buf.getDouble() //u
            buf.getDouble() //v

            if (!useDanColor) buf.getFloat() //r
            if (!useDanColor) buf.getFloat() //g
            if (!useDanColor) buf.getFloat() //b
            if (!useDanColor) buf.getFloat() //a

            buf.getFloat() //nx
            buf.getFloat() //ny
            buf.getFloat() //nz

            verifyVertex(j + 1)
          }
        }

        verifyVertex(0)
        verifyPiece(i + 1)
      }
    }

    verifyPiece(0)
  }
}
