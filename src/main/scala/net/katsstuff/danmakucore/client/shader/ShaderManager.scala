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

import scala.collection.mutable
import scala.util.control.NonFatal

import net.katsstuff.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.danmakucore.helper.LogHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.{IResourceManager, IResourceManagerReloadListener}
import net.minecraft.crash.CrashReport
import net.minecraft.util.{ReportedException, ResourceLocation}
import net.minecraftforge.fml.common.ProgressManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class ShaderManager(resourceManager: IResourceManager) extends IResourceManagerReloadListener {
  val shaderPrograms = mutable.Map.empty[ResourceLocation, DanCoreShaderProgram]
  val shaderObjects  = mutable.Map.empty[ResourceLocation, DanCoreShader]
  DanCoreRenderHelper.registerResourceReloadListener(this)

  def getShader(location: ResourceLocation, shaderType: ShaderType): DanCoreShader =
    shaderObjects.getOrElseUpdate(location, DanCoreShader.compileShader(location, shaderType, resourceManager))

  def createShaderProgram(
      location: ResourceLocation,
      shaderTypes: Seq[ShaderType],
      uniforms: Seq[UniformBase]
  ): DanCoreShaderProgram = {
    try {
      val shaders = shaderTypes.map {
        case tpe @ ShaderType(_, extension) =>
          val shaderLocation = new ResourceLocation(s"$location.$extension")
          val shader         = getShader(shaderLocation, tpe)
          shaderLocation -> shader
      }.toMap

      DanCoreShaderProgram.create(shaders, uniforms)
    } catch {
      case e: IOException =>
        LogHelper.warn(s"Failed to load shader: $location", e)
        DanCoreShaderProgram.MissingShaderProgram
      case e: ShaderException =>
        LogHelper.warn(s"Failed to create shader: $location", e)
        DanCoreShaderProgram.MissingShaderProgram
      case NonFatal(throwable) =>
        val crashReport         = CrashReport.makeCrashReport(throwable, "Registering texture")
        val crashReportCategory = crashReport.makeCategory("Resource location being registered")
        crashReportCategory.addCrashSection("Resource location", location)
        throw new ReportedException(crashReport)
    }
  }

  def initShader(
      shaderLocation: ResourceLocation,
      shaderTypes: Seq[ShaderType],
      uniforms: Seq[UniformBase]
  ): DanCoreShaderProgram =
    shaderPrograms.getOrElseUpdate(shaderLocation, createShaderProgram(shaderLocation, shaderTypes, uniforms))

  def getShaderProgram(shaderLocation: ResourceLocation): Option[DanCoreShaderProgram] =
    shaderPrograms.get(shaderLocation)

  override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {
    val reloadBar = ProgressManager.push("Reloading Shader Manager", 0)

    val shaderBar = ProgressManager.push("Reloading shaders", shaderObjects.size)
    val newShaders = for ((resource, shader) <- shaderObjects) yield {
      shaderBar.step(resource.toString)
      shader.delete()
      resource -> getShader(resource, shader.shaderType)
    }

    shaderObjects.clear()
    shaderObjects ++= newShaders
    ProgressManager.pop(shaderBar)

    val programBar = ProgressManager.push("Reloading shader programs", shaderPrograms.size)
    val res = for ((resource, program) <- shaderPrograms) yield {
      programBar.step(resource.toString)
      program.delete()
      resource -> createShaderProgram(resource, program.shaders.map(_.shaderType), program.uniforms.map {
        case (name, DanCoreUniform(_, tpe, count, _, _)) => UniformBase(name, tpe, count)
      }.toSeq)
    }

    shaderPrograms ++= res
    ProgressManager.pop(reloadBar)
  }
}

@SideOnly(Side.CLIENT)
object ShaderManager extends ShaderManager(Minecraft.getMinecraft.getResourceManager)
