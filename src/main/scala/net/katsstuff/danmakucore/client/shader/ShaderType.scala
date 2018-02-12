/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.shader

import net.minecraft.client.renderer.OpenGlHelper

sealed abstract case class ShaderType(constant: Int, extension: String)
object ShaderType {
  object Vertex   extends ShaderType(OpenGlHelper.GL_VERTEX_SHADER, "vsh")
  object Fragment extends ShaderType(OpenGlHelper.GL_FRAGMENT_SHADER, "fsh")
}
