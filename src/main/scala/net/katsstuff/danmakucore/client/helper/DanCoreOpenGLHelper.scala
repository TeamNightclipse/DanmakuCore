/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.helper

import org.lwjgl.opengl.{ARBBufferObject, GL15, GLContext}

import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
object DanCoreOpenGLHelper {

  import net.minecraft.client.renderer.OpenGlHelper._

  private val contextcapabilities = GLContext.getCapabilities
  private val arbVbo              = !contextcapabilities.OpenGL15 && contextcapabilities.GL_ARB_vertex_buffer_object

  val GL_STREAM_DRAW:  Int = assignVboValue(GL15.GL_STREAM_DRAW, ARBBufferObject.GL_STREAM_DRAW_ARB)
  val GL_STREAM_READ:  Int = assignVboValue(GL15.GL_STREAM_READ, ARBBufferObject.GL_STREAM_READ_ARB)
  val GL_STREAM_COPY:  Int = assignVboValue(GL15.GL_STREAM_COPY, ARBBufferObject.GL_STREAM_COPY_ARB)
  val GL_STATIC_DRAW:  Int = assignVboValue(GL15.GL_STATIC_DRAW, ARBBufferObject.GL_STATIC_DRAW_ARB)
  val GL_STATIC_READ:  Int = assignVboValue(GL15.GL_STATIC_READ, ARBBufferObject.GL_STATIC_READ_ARB)
  val GL_STATIC_COPY:  Int = assignVboValue(GL15.GL_STATIC_COPY, ARBBufferObject.GL_STATIC_COPY_ARB)
  val GL_DYNAMIC_DRAW: Int = assignVboValue(GL15.GL_DYNAMIC_DRAW, ARBBufferObject.GL_DYNAMIC_DRAW_ARB)
  val GL_DYNAMIC_READ: Int = assignVboValue(GL15.GL_DYNAMIC_READ, ARBBufferObject.GL_DYNAMIC_READ_ARB)
  val GL_DYNAMIC_COPY: Int = assignVboValue(GL15.GL_DYNAMIC_COPY, ARBBufferObject.GL_DYNAMIC_COPY_ARB)

  private def assignVboValue(glValue: Int, arbValue: Int): Int =
    if (vboSupported) if (arbVbo) arbValue else glValue else 0
}
