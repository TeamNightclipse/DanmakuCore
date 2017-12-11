/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.render

import java.nio.ByteBuffer

import org.lwjgl.opengl.{GL11, GL15}

import net.minecraft.client.renderer.{GlStateManager, OpenGlHelper}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class ArrayBuffer(count: Int, target: Int, usage: Int) {
  private val id      = OpenGlHelper.glGenBuffers()
  private var deleted = false

  def bindBuffer(): Unit = {
    if (deleted) throw new IllegalStateException("Deleted")
    OpenGlHelper.glBindBuffer(target, id)
  }

  def unbindBuffer(): Unit = {
    if (deleted) throw new IllegalStateException("Deleted")
    OpenGlHelper.glBindBuffer(target, 0)
  }

  def bufferData(data: ByteBuffer): Unit = {
    bindBuffer()
    OpenGlHelper.glBufferData(target, data, usage)
    unbindBuffer()
  }

  def bufferSubData(offset: Int, data: ByteBuffer): Unit = {
    bindBuffer()
    GL15.glBufferSubData(target, offset, data)
    unbindBuffer()
  }

  def drawArrays(mode: Int): Unit = {
    if (deleted) throw new IllegalStateException("Deleted")
    GlStateManager.glDrawArrays(mode, 0, count)
  }

  def drawElements(mode: Int, tpe: Int, offset: Int): Unit = {
    if (deleted) throw new IllegalStateException("Deleted")
    bindBuffer()
    GL11.glDrawElements(mode, count, tpe, offset)
  }

  def deleteGlBuffers(): Unit = {
    if (!deleted) {
      OpenGlHelper.glDeleteBuffers(id)
      deleted = true
    }
  }
}
