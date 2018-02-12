/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.shader

import java.io.IOException

import scala.collection.JavaConverters._

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.lwjgl.BufferUtils

import net.katsstuff.danmakucore.helper.LogHelper
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.{IResource, IResourceManager}
import net.minecraft.util.ResourceLocation

case class DanCoreShader(id: Int, shaderType: ShaderType) {
  private var deleted: Boolean = false

  def delete(): Unit = {
    if (!deleted) {
      OpenGlHelper.glDeleteShader(id)
      deleted = true
    }
  }
}
object DanCoreShader {

  private val Include = """#pragma include "(.+)"""".r

  def missingShader(tpe: ShaderType) = DanCoreShader(0, tpe)

  @throws[ShaderException]
  @throws[IOException]
  def compileShader(
      location: ResourceLocation,
      shaderType: ShaderType,
      resourceManager: IResourceManager
  ): DanCoreShader = {
    val shaderSource = parseShader(location, resourceManager).mkString("\n")

    val buffer = BufferUtils.createByteBuffer(shaderSource.length)
    buffer.put(shaderSource.getBytes)
    buffer.flip()
    val shaderId = OpenGlHelper.glCreateShader(shaderType.constant)
    OpenGlHelper.glShaderSource(shaderId, buffer)
    OpenGlHelper.glCompileShader(shaderId)

    if (OpenGlHelper.glGetShaderi(shaderId, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
      val s = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(shaderId, 32768))
      throw new ShaderException(s"Couldn't compile $location\nError: $s")
    }
    DanCoreShader(shaderId, shaderType)
  }

  @throws[IOException]
  def parseShader(location: ResourceLocation, resourceManager: IResourceManager): Seq[String] = {
    var resource: IResource = null
    try {
      resource = resourceManager.getResource(location)
      val lines = IOUtils.readLines(resource.getInputStream, "UTF-8").asScala
      lines.flatMap {
        case Include(file) =>
          val includeLocation = if (file.contains(':')) {
            new ResourceLocation(file)
          } else {
            resource.getInputStream
            val path      = location.getResourcePath
            val folderIdx = path.lastIndexOf('/')
            if (folderIdx != -1) {
              val folder = path.substring(0, folderIdx)
              new ResourceLocation(location.getResourceDomain, s"$folder/$file")
            } else {
              new ResourceLocation(location.getResourceDomain, file)
            }
          }

          parseShader(includeLocation, resourceManager)
        case other => Seq(other)
      }
    } finally {
      IOUtils.closeQuietly(resource)
    }
  }
}
