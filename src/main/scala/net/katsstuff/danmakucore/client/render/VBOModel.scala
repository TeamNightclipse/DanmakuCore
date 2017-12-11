/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.render

import scala.collection.JavaConverters._

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._

import net.katsstuff.danmakucore.helper.LogHelper
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage._
import net.minecraft.client.renderer.vertex.{VertexFormat, VertexFormatElement}
import net.minecraft.client.renderer.{GlStateManager, OpenGlHelper}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

//Mix of WorldVertexBufferUploader and ForgeHooksClient
@SideOnly(Side.CLIENT)
case class VBOModel(format: VertexFormat, arrayBuffer: DanCoreArrayBuffer, vertexCount: Int, mode: Int) {

  def draw(): Unit = {
    if (vertexCount > 0) {
      val elements = format.getElements.asScala
      arrayBuffer.bindBuffer()

      for (i <- elements.indices) {
        preDraw(elements(i).getUsage, format, i, format.getSize)
      }

      arrayBuffer.drawArrays(mode)
      arrayBuffer.unbindBuffer()

      for (i <- elements.indices) {
        postDraw(elements(i).getUsage, format, i)
      }
    }
  }

  private def preDraw(usage: VertexFormatElement.EnumUsage, format: VertexFormat, element: Int, stride: Int): Unit = {
    val attr   = format.getElement(element)
    val count  = attr.getElementCount
    val tpe    = attr.getType.getGlConstant
    val offset = format.getOffset(element)
    usage match {
      case POSITION =>
        glVertexPointer(count, tpe, stride, offset)
        glEnableClientState(GL_VERTEX_ARRAY)
      case NORMAL =>
        if (count != 3) throw new IllegalArgumentException("Normal attribute should have the size 3: " + attr)
        glNormalPointer(tpe, stride, offset)
        glEnableClientState(GL_NORMAL_ARRAY)
      case COLOR =>
        glColorPointer(count, tpe, stride, offset)
        glEnableClientState(GL_COLOR_ARRAY)
      case UV =>
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex)
        glTexCoordPointer(count, tpe, stride, offset)
        glEnableClientState(GL_TEXTURE_COORD_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
      case PADDING =>
      case GENERIC =>
        glEnableVertexAttribArray(attr.getIndex)
        glVertexAttribPointer(attr.getIndex, count, tpe, false, stride, offset)
      case _ => LogHelper.fatal(s"Unimplemented vanilla attribute upload: ${usage.getDisplayName}")
    }
  }

  private def postDraw(usage: VertexFormatElement.EnumUsage, format: VertexFormat, element: Int): Unit = {
    val attr = format.getElement(element)
    usage match {
      case POSITION =>
        glDisableClientState(GL_VERTEX_ARRAY)
      case NORMAL =>
        glDisableClientState(GL_NORMAL_ARRAY)
      case COLOR =>
        glDisableClientState(GL_COLOR_ARRAY)
        // is this really needed?
        GlStateManager.resetColor()
      case UV =>
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex)
        glDisableClientState(GL_TEXTURE_COORD_ARRAY)
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit)
      case PADDING =>
      case GENERIC =>
        glDisableVertexAttribArray(attr.getIndex)
      case _ => LogHelper.fatal(s"Unimplemented vanilla attribute upload: ${usage.getDisplayName}")
    }
  }
}
