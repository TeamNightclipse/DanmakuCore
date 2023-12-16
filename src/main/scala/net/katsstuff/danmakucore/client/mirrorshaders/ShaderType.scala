package net.katsstuff.danmakucore.client.mirrorshaders

import com.mojang.blaze3d.platform.GlConst

/**
  * Represents a type of shader. Mirror currently supports vertex and fragment
  * shaders.
  * @param constant
  *   The OpenGL constant for this shader type.
  * @param extension
  *   The extension used by this shader type.
  */
sealed abstract case class ShaderType(constant: Int, extension: String)
object ShaderType {
  object Vertex extends ShaderType(GlConst.GL_VERTEX_SHADER, "vsh")
  def vertex: Vertex.type = Vertex

  object Fragment extends ShaderType(GlConst.GL_FRAGMENT_SHADER, "fsh")
  def fragment: Fragment.type = Fragment
}
