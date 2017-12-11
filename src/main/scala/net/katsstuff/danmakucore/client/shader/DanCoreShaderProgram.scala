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
import net.minecraft.util.ResourceLocation

case class DanCoreShaderProgram(shaders: Seq[DanCoreShader], programId: Int, uniforms: Map[String, DanCoreUniform]) {

  def delete(): Unit = {
    shaders.foreach(_.delete())
    OpenGlHelper.glDeleteProgram(programId)
  }

  def begin(): Unit =
    OpenGlHelper.glUseProgram(programId)

  def getUniform(name: String): Option[DanCoreUniform] = uniforms.get(name)

  def uploadUniforms(): Unit = uniforms.values.foreach(_.upload())

  def end(): Unit = OpenGlHelper.glUseProgram(0)
}
object DanCoreShaderProgram {

  val MissingShaderProgram = DanCoreShaderProgram(Nil, 0, Map.empty)

  def create(shaders: Map[ResourceLocation, DanCoreShader], uniforms: Seq[UniformBase]): DanCoreShaderProgram = {
    val programId = OpenGlHelper.glCreateProgram()

    shaders.values.foreach { shader =>
      OpenGlHelper.glAttachShader(programId, shader.id)
    }
    OpenGlHelper.glLinkProgram(programId)
    val errorId = OpenGlHelper.glGetProgrami(programId, OpenGlHelper.GL_LINK_STATUS)

    if (errorId == 0) {
      throw new ShaderException(s"""|Error encountered when linking program containing shaders: $shaders. Log output:
            |${OpenGlHelper.glGetProgramInfoLog(programId, 32768)}""".stripMargin)
    }

    val uniformMap = uniforms.map {
      case UniformBase(name, tpe, count) =>
        val location = OpenGlHelper.glGetUniformLocation(programId, name)
        name -> DanCoreUniform.create(location, tpe, count)
    }.toMap

    DanCoreShaderProgram(shaders.values.toSeq, programId, uniformMap)
  }

}
