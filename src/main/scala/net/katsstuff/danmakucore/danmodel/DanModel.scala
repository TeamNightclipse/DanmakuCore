/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmodel

import java.nio.ByteBuffer

import scala.annotation.tailrec

import org.lwjgl.opengl.GL11

import net.minecraft.client.renderer.{GlStateManager, Tessellator, VertexBuffer}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats

class DanModel(private[this] val data: Array[Byte], private[this] val pieces: Int, private[this] val danAlpha: Float) {

  def render(vb: VertexBuffer, tes: Tessellator, danmakuColor: Int): Unit = {
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

            vb.pos(x, y, z)
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
        vb.begin(glMode, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL)

        renderVertex(0)

        tes.draw()

        if (useDanColor) {
          GlStateManager.depthMask(true)
          GlStateManager.disableBlend()
        }

        renderPiece(i + 1)
      }
    }

    renderPiece(0)
  }
}
