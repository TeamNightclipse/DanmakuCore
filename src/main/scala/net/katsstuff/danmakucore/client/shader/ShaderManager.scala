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
  private val shaderPrograms = mutable.Map.empty[ResourceLocation, DanCoreShaderProgram]
  private val shaderProgramsInits = {
    type ShaderInit = DanCoreShaderProgram => Unit
    new mutable.HashMap[ResourceLocation, mutable.Set[ShaderInit]] with mutable.MultiMap[ResourceLocation, ShaderInit]
  }
  private val shaderObjects = mutable.Map.empty[ResourceLocation, DanCoreShader]
  DanCoreRenderHelper.registerResourceReloadListener(this)

  def getShader(location: ResourceLocation, shaderType: ShaderType): DanCoreShader =
    shaderObjects.getOrElseUpdate(location, compileShader(location, shaderType))

  def compileShader(location: ResourceLocation, shaderType: ShaderType): DanCoreShader = {
    try {
      DanCoreShader.compileShader(location, shaderType, resourceManager)
    } catch {
      case e: IOException =>
        LogHelper.warn(s"Failed to load shader: $location", e)
        DanCoreShader.missingShader(shaderType)
      case e: ShaderException =>
        LogHelper.warn(s"Failed to compile shader: $location", e)
        DanCoreShader.missingShader(shaderType)
    }
  }

  def createShaderProgram(
      location: ResourceLocation,
      shaderTypes: Seq[ShaderType],
      uniforms: Seq[UniformBase],
      strictUniforms: Boolean = false
  ): DanCoreShaderProgram = {
    val shaders = shaderTypes.map(_ -> location).toMap
    createShaderProgram(shaders, uniforms, strictUniforms)
  }

  def createShaderProgram(
      shaders: Map[ShaderType, ResourceLocation],
      uniforms: Seq[UniformBase],
      strictUniforms: Boolean
  ): DanCoreShaderProgram = {
    val newShaders = shaders.map {
      case (tpe, location) =>
        val shaderLocation = new ResourceLocation(s"$location.${tpe.extension}")
        val shader         = getShader(shaderLocation, tpe)
        shaderLocation -> shader
    }

    try {
      DanCoreShaderProgram.create(newShaders, uniforms, strictUniforms)
    } catch {
      case e: ShaderException =>
        LogHelper.warn(s"Failed to create shader: $shaders", e)
        DanCoreShaderProgram.missingShaderProgram(newShaders.values.toSeq, uniforms)
      case NonFatal(throwable) =>
        val crashReport         = CrashReport.makeCrashReport(throwable, "Registering shaders")
        val crashReportCategory = crashReport.makeCategory("Resource locations being registered")
        crashReportCategory.addCrashSection("Resource locations", shaders.values)
        throw new ReportedException(crashReport)
    }
  }

  def initShader(
      shaderLocation: ResourceLocation,
      shaderTypes: Seq[ShaderType],
      uniforms: Seq[UniformBase],
      init: DanCoreShaderProgram => Unit
  ): Unit = {
    shaderProgramsInits.addBinding(shaderLocation, init)
    val shader = shaderPrograms.getOrElseUpdate(shaderLocation, createShaderProgram(shaderLocation, shaderTypes, uniforms))
    init(shader)
  }

  def getShaderProgram(shaderLocation: ResourceLocation): Option[DanCoreShaderProgram] =
    shaderPrograms.get(shaderLocation)

  override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {
    val reloadBar = ProgressManager.push("Reloading Shader Manager", 0)

    val shaderBar = ProgressManager.push("Reloading shaders", shaderObjects.size)
    val newShaders = for ((resource, shader) <- shaderObjects) yield {
      shaderBar.step(resource.toString)
      shader.delete()
      resource -> compileShader(resource, shader.shaderType)
    }

    shaderObjects.clear()
    shaderObjects ++= newShaders
    ProgressManager.pop(shaderBar)

    val programBar = ProgressManager.push("Reloading shader programs", shaderPrograms.size)
    val res = for ((resource, program) <- shaderPrograms) yield {
      programBar.step(resource.toString)
      program.delete()
      val newProgram = createShaderProgram(resource, program.shaders.map(_.shaderType), program.uniformMap.map {
        case (name, uniform) => UniformBase(name, uniform.tpe, uniform.count)
      }.toSeq)
      shaderProgramsInits.get(resource).foreach(_.foreach(init => init(newProgram)))

      resource -> newProgram
    }

    shaderPrograms ++= res
    ProgressManager.pop(reloadBar)
  }
}

@SideOnly(Side.CLIENT)
object ShaderManager extends ShaderManager(Minecraft.getMinecraft.getResourceManager)
