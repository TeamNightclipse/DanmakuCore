package net.katsstuff.danmakucore.client.mirrorshaders

import java.util
import java.util.Optional

import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

import com.mojang.blaze3d.platform.{GlConst, GlStateManager}
import net.minecraft.resources.ResourceLocation

/**
  * Represents a Mirror shader program.
  * @param shaders
  *   The shaders used by this program.
  * @param programId
  *   The program id.
  * @param uniformMap
  *   A map of the uniforms of this program.
  * @param strictUniforms
  *   If this program should error if its uniforms are missing.
  */
case class MirrorShaderProgram(
    shaders: Seq[MirrorShader],
    programId: Int,
    uniformMap: Map[String, MirrorUniform[_ <: UniformType]],
    strictUniforms: Boolean
) {

  /**
    * Deletes this program, and all the shaders it uses. Be careful with this
    * one.
    */
  def delete(): Unit = {
    shaders.foreach(_.delete())
    GlStateManager.glDeleteProgram(programId)
  }

  /** Begins to use this program. */
  def begin(): Unit =
    GlStateManager._glUseProgram(programId)

  /**
    * Gets a uniform. Scala-API.
    * @param name
    *   The name of the uniform.
    */
  def getUniformS(name: String): Option[MirrorUniform[_ <: UniformType]] = uniformMap.get(name)

  /**
    * Gets a uniform. Java-API.
    * @param name
    *   The name of the uniform.
    */
  def getUniformJ(name: String): Optional[MirrorUniform[_ <: UniformType]] = uniformMap.get(name).toJava

  /** Uploads any dirty uniforms. */
  def uploadUniforms(): Unit = uniformMap.values.foreach(_.upload())

  /** Ends the use of this program. */
  def end(): Unit = GlStateManager._glUseProgram(0)
}
object MirrorShaderProgram {

  private[mirrorshaders] def missingShaderProgram(
      shaders: Seq[MirrorShader],
      uniforms: Map[String, UniformBase[_ <: UniformType]]
  ) =
    MirrorShaderProgram(
      shaders,
      0,
      uniforms.map { case (name, base) => name -> new NOOPUniform(base.tpe, base.count) },
      strictUniforms = false
    )

  /**
    * Creates a new shader program with no management. Java-API.
    * @param shaders
    *   A map of where to find shaders, and the shaders themselves.
    * @param uniforms
    *   A map of uniform names and a base which the uniforms will be built on.
    * @param strictUniforms
    *   If the uniforms should be strict (missing uniform == exception).
    */
  @throws[ShaderException]
  def create(
      shaders: util.Map[ResourceLocation, MirrorShader],
      uniforms: util.Map[String, UniformBase[_ <: UniformType]],
      strictUniforms: Boolean
  ): MirrorShaderProgram = create(shaders.asScala.toMap, uniforms.asScala.toMap, strictUniforms)

  /**
    * Creates a new shader program with no management. Scala-API.
    * @param shaders
    *   A map of where to find shaders, and the shaders themselves.
    * @param uniforms
    *   A map of uniform names and a base which the uniforms will be built on.
    * @param strictUniforms
    *   If the uniforms should be strict (missing uniform == exception).
    */
  @throws[ShaderException]
  def create(
      shaders: Map[ResourceLocation, MirrorShader],
      uniforms: Map[String, UniformBase[_ <: UniformType]],
      strictUniforms: Boolean = true
  ): MirrorShaderProgram = {
    val programId = GlStateManager.glCreateProgram()

    shaders.values.foreach { shader =>
      GlStateManager.glAttachShader(programId, shader.id)
    }
    GlStateManager.glLinkProgram(programId)
    val errorId = GlStateManager.glGetProgrami(programId, GlConst.GL_LINK_STATUS)

    if (errorId == 0) {
      throw new ShaderException(s"""|Error encountered when linking program containing shaders: $shaders. Log output:
                                    |${GlStateManager.glGetProgramInfoLog(programId, 32768)}""".stripMargin)
    }

    val uniformMap = uniforms.map { case (name, UniformBase(tpe, count)) =>
      val location = GlStateManager._glGetUniformLocation(programId, name)
      if (location == -1) {
        if (strictUniforms) {
          throw new ShaderException("Error when getting uniform location")
        } else new NOOPUniform(tpe, count)
      }
      name -> MirrorUniform.create(location, tpe, count)
    }

    MirrorShaderProgram(shaders.values.toSeq, programId, uniformMap, strictUniforms)
  }
}
