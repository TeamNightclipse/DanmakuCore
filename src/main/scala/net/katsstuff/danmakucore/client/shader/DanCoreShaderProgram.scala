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

case class DanCoreShaderProgram(shaders: Seq[DanCoreShader], programId: Int, uniformMap: Map[String, DanCoreUniform]) {

  def delete(): Unit = {
    shaders.foreach(_.delete())
    OpenGlHelper.glDeleteProgram(programId)
  }

  def begin(): Unit =
    OpenGlHelper.glUseProgram(programId)

  def getUniform(name: String): Option[DanCoreUniform] = uniformMap.get(name)

  def uploadUniforms(): Unit = uniformMap.values.foreach(_.upload())

  def end(): Unit = OpenGlHelper.glUseProgram(0)

  def uniforms: UniformSyntax = new UniformSyntax(this)
}
object DanCoreShaderProgram {

  def missingShaderProgram(shaders: Seq[DanCoreShader], uniforms: Seq[UniformBase]) =
    DanCoreShaderProgram(shaders, 0, uniforms.map(base => base.name -> new NOOPUniform(base.tpe, base.count)).toMap)

  @throws[ShaderException]
  def create(
      shaders: Map[ResourceLocation, DanCoreShader],
      uniforms: Seq[UniformBase],
      strictUniforms: Boolean = false
  ): DanCoreShaderProgram = {
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
        if (location == -1) {
          if (strictUniforms) {
            throw new ShaderException("Error when getting uniform location")
          } else new NOOPUniform(tpe, count)
        }
        name -> DanCoreUniform.create(location, tpe, count)
    }.toMap

    DanCoreShaderProgram(shaders.values.toSeq, programId, uniformMap)
  }

}
