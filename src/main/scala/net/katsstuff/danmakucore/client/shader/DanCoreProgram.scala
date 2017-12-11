/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.shader

import net.katsstuff.danmakucore.helper.LogHelper
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation

case class DanCoreProgram(vertexId: Int, fragmentId: Int, programId: Int, uniforms: Map[String, DanCoreUniform]) {

  def delete(): Unit = {
    OpenGlHelper.glDeleteShader(vertexId)
    OpenGlHelper.glDeleteShader(fragmentId)
    OpenGlHelper.glDeleteProgram(programId)
  }

  def begin(): Unit = OpenGlHelper.glUseProgram(programId)

  def getUniform(name: String): Option[DanCoreUniform] = uniforms.get(name)

  def uploadUniforms(): Unit = uniforms.values.foreach(_.upload())

  def end(): Unit = OpenGlHelper.glUseProgram(0)
}
object DanCoreProgram {

  val MissingShader = DanCoreProgram(0, 0, 0, Map.empty)

  def create(
      vertexId: Int,
      vertexShaderLocation: ResourceLocation,
      fragmentId: Int,
      fragmentShaderLocation: ResourceLocation,
      uniforms: Seq[UniformBase]
  ): DanCoreProgram = {
    val programId = OpenGlHelper.glCreateProgram()

    OpenGlHelper.glAttachShader(programId, vertexId)
    OpenGlHelper.glAttachShader(programId, fragmentId)
    OpenGlHelper.glLinkProgram(programId)
    val errorId = OpenGlHelper.glGetProgrami(programId, OpenGlHelper.GL_LINK_STATUS)

    if (errorId == 0) {
      LogHelper.warn(
        s"Error encountered when linking program containing VS $vertexShaderLocation and FS $fragmentShaderLocation. Log output:"
      )
      LogHelper.warn(OpenGlHelper.glGetProgramInfoLog(programId, 32768))
    }

    val uniformMap = uniforms.map {
      case UniformBase(name, tpe, count) =>
        val location = OpenGlHelper.glGetUniformLocation(programId, name)
        name -> DanCoreUniform.create(location, tpe, count)
    }.toMap

    DanCoreProgram(vertexId, fragmentId, programId, uniformMap)
  }

}
