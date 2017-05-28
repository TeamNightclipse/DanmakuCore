/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmodel

import java.io.{DataInput, DataInputStream}
import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}

import scala.annotation.tailrec
import scala.util.Try

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.{Form, IRenderForm}
import net.katsstuff.danmakucore.impl.form.FormGeneric
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation

object DanModelReader {

  def createForm(resource: ResourceLocation): Try[Form] = {
    readModel(resource).map { case (_, model) =>
        new FormGeneric() {
          override protected def createRenderer(): IRenderForm = new IRenderForm {
            override def renderForm(danmaku: EntityDanmaku, x: Double, y: Double, z: Double,
                entityYaw: Float, partialTicks: Float, man: RenderManager): Unit = {
              val tes = Tessellator.getInstance
              val vb = tes.getBuffer
              val pitch = danmaku.rotationPitch
              val yaw = danmaku.rotationYaw
              val roll = danmaku.getRoll
              val shotData = danmaku.getShotData
              val sizeX = shotData.getSizeX
              val sizeY = shotData.getSizeY
              val sizeZ = shotData.getSizeZ
              val color = shotData.getColor

              GL11.glRotatef(-yaw, 0F, 1F, 0F)
              GL11.glRotatef(pitch, 1F, 0F, 0F)
              GL11.glRotatef(roll, 0F, 0F, 1F)
              GL11.glScalef(sizeX, sizeY, sizeZ)

              model.render(vb, tes, color)
            }
          }
        }
    }
  }

  def readModel(resource: ResourceLocation): Try[(DanModelDescription, DanModel)] = Try {
    val url = getClass.getResource(s"assets/${resource.getResourceDomain}/${resource.getResourcePath}")
    val is: DataInput = new DataInputStream(Files.newInputStream(Paths.get(url.toURI)))


    val danModel = readString(is)
    if(danModel != "DanmakuCore DanModel") throw DanModelReadException("Not a DanModel file")
    val version = is.readShort()
    if(version != 1) throw DanModelReadException(s"Unknown version $version")

    val name = readString(is)
    val description = readString(is)
    val author = readString(is)

    val modelDescription = DanModelDescription(name, description, author)

    val pieces = is.readInt()
    val danAlpha = is.readFloat()

    val modelLength = is.readInt()
    val data = new Array[Byte](modelLength)
    is.readFully(data)

    verifyData(data, pieces)

    val model = new DanModel(data, pieces, danAlpha)

    (modelDescription, model)
  }

  def readString(is: DataInput): String = {
    val array = new Array[Byte](is.readShort())
    is.readFully(array)
    new String(array, "UTF-8")
  }

  def readString(buf: ByteBuffer): String = {
    val array = new Array[Byte](buf.getShort)
    buf.get(array)
    new String(array, "UTF-8")
  }

  def verifyData(data: Array[Byte], pieces: Int): Unit = {
    val buf = ByteBuffer.wrap(data)

    @tailrec
    def verifyPiece(i: Int): Unit = {
      if(i < pieces) {
        buf.getInt() //glMode
        val vertices = buf.getInt()
        val useDanColor = buf.get() != 0

        @tailrec
        def verifyVertex(j: Int): Unit = {
          if(j < vertices) {
            buf.getDouble() //x
            buf.getDouble() //y
            buf.getDouble() //z

            buf.getDouble() //u
            buf.getDouble() //v

            if(!useDanColor) buf.getFloat() //r
            if(!useDanColor) buf.getFloat() //g
            if(!useDanColor) buf.getFloat() //b
            if(!useDanColor) buf.getFloat() //a

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
