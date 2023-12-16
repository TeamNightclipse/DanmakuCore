package net.katsstuff.danmakucore.client.mirrorshaders

import java.io.IOException

import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._
import scala.jdk.StreamConverters._
import scala.util.Using

import com.mojang.blaze3d.platform.{GlConst, GlStateManager}
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager

/**
  * Represents an individual shader within Mirror.
  * @param id
  *   The id of the shader.
  * @param shaderType
  *   The type of the shader.
  */
class MirrorShader(val id: Int, val shaderType: ShaderType) {
  private var deleted: Boolean = false

  /** Deletes this shader. Be very careful when using this. */
  def delete(): Unit =
    if (!deleted) {
      GlStateManager.glDeleteShader(id)
      deleted = true
    }
}
object MirrorShader {

  private val Include = """#pragma Mirror include "(.+)"""".r

  private[mirrorshaders] def missingShader(tpe: ShaderType) = new MirrorShader(0, tpe)

  /**
    * Compiles an individual shader. If you use this method, you're on your own.
    * @param location
    *   The location of the shader.
    * @param shaderType
    *   The shader type to compile.
    * @param resourceManager
    *   A resource manager to load the shader from.
    */
  @throws[ShaderException]
  @throws[IOException]
  def compile(
      location: ResourceLocation,
      shaderType: ShaderType,
      resourceManager: ResourceManager
  ): MirrorShader = {
    val shaderSource = parseShaderSource(location, resourceManager)
    compileFromShaderSource(location, shaderSource, shaderType)
  }

  def compileFromShaderSource(location: ResourceLocation, shaderSource: Seq[String], shaderType: ShaderType): MirrorShader = {
    val shaderId = GlStateManager.glCreateShader(shaderType.constant)
    GlStateManager.glShaderSource(shaderId, shaderSource.map(s => s + "\n").asJava)
    GlStateManager.glCompileShader(shaderId)
    println(shaderSource)

    if (GlStateManager.glGetShaderi(shaderId, GlConst.GL_COMPILE_STATUS) == 0) {
      val s = GlStateManager.glGetShaderInfoLog(shaderId, 32768).trim
      throw new ShaderException(s"Couldn't compile $location\nError: $s")
    }
    new MirrorShader(shaderId, shaderType)
  }

  @throws[IOException]
  def parseShaderSource(location: ResourceLocation, resourceManager: ResourceManager): Seq[String] = {
    val resource = resourceManager
      .getResource(location)
      .toScala
      .getOrElse(
        throw new IOException(s"Resource $location not found")
      )

    Using(resource.openAsReader()) { reader =>
      reader.lines().toScala(Seq)
    }.get.flatMap {
      case Include(file) =>
        val includeLocation = if (file.contains(':')) {
          new ResourceLocation(file)
        } else {
          val path      = location.getPath
          val folderIdx = path.lastIndexOf('/')
          if (folderIdx != -1) {
            val folder = path.substring(0, folderIdx)
            new ResourceLocation(location.getNamespace, s"$folder/$file")
          } else {
            new ResourceLocation(location.getNamespace, file)
          }
        }

        parseShaderSource(includeLocation, resourceManager)
      case other => Seq(other)
    }
  }
}
