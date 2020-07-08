/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.danmodel

import java.nio.ByteBuffer

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

import org.lwjgl.opengl.GL11

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.mirror.client.render.{MirrorArrayBuffer, VBOModel}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BufferBuilder, GlStateManager, OpenGlHelper, Tessellator}

class DanModel(private[this] val data: Array[Byte], private[this] val pieces: Int, private[this] val danAlpha: Float) {

  private val models        = ArrayBuffer.empty[VBOModel]
  private var generatingVBO = false

  def render(bb: BufferBuilder, danmakuColor: Int): Unit = {
    val buf = ByteBuffer.wrap(data)

    val danRed   = (danmakuColor >> 16 & 255) / 255F
    val danGreen = (danmakuColor >> 8 & 255) / 255F
    val danBlue  = (danmakuColor & 255) / 255F

    @tailrec
    def renderPiece(i: Int): Unit = {
      if (i < pieces) {
        val glMode      = buf.getInt()
        val vertices    = buf.getInt()
        val useDanColor = buf.get() != 0

        @tailrec
        def renderVertex(j: Int): Unit = {
          if (j < vertices) {
            val x = buf.getDouble()
            val y = buf.getDouble()
            val z = buf.getDouble()

            val u = buf.getDouble()
            val v = buf.getDouble()

            val r = if (useDanColor) danRed else buf.getFloat()
            val g = if (useDanColor) danGreen else buf.getFloat()
            val b = if (useDanColor) danBlue else buf.getFloat()
            val a = if (useDanColor) danAlpha else buf.getFloat()

            val nx = buf.getFloat()
            val ny = buf.getFloat()
            val nz = buf.getFloat()

            bb.pos(x, y, z)
              .tex(u, v)
              .color(r, g, b, a)
              .normal(nx, ny, nz)
              .endVertex()

            renderVertex(j + 1)
          }
        }

        if (useDanColor) {
          GlStateManager.enableBlend()
          GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
          GlStateManager.depthMask(false)
        }
        bb.begin(glMode, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL)

        renderVertex(0)

        drawBuffer(bb)

        if (useDanColor) {
          GlStateManager.depthMask(true)
          GlStateManager.disableBlend()
        }

        renderPiece(i + 1)
      }
    }

    renderPiece(0)
  }

  def drawBuffer(bb: BufferBuilder): Unit = {
    if (generatingVBO) {
      bb.finishDrawing()
      val format = bb.getVertexFormat
      val data   = bb.getByteBuffer
      val count  = data.limit / format.getElementCount
      val buffer = new MirrorArrayBuffer(count, OpenGlHelper.GL_ARRAY_BUFFER, OpenGlHelper.GL_STATIC_DRAW)
      buffer.bufferData(data)
      val vboModel = VBOModel(format, buffer, bb.getVertexCount, bb.getDrawMode)
      bb.reset()
      models += vboModel
    } else {
      Tessellator.getInstance().draw()
    }
  }

  def generateVBOs(): Unit = {
    deleteVBOs()
    generatingVBO = true
    render(Tessellator.getInstance().getBuffer, DanCoreRenderHelper.OverwriteColorEdge)
    generatingVBO = false
  }

  def drawVBOs(): Unit =
    models.foreach(_.draw())

  def deleteVBOs(): Unit = {
    models.foreach(_.arrayBuffer.delete())
    models.clear()
  }
}
