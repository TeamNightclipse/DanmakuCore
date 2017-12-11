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
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.{IResourceManager, IResourceManagerReloadListener}
import net.minecraft.crash.CrashReport
import net.minecraft.util.{ReportedException, ResourceLocation}
import net.minecraftforge.fml.common.ProgressManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class ShaderManager(resourceManager: IResourceManager) extends IResourceManagerReloadListener {
  val shaderObjects         = mutable.Map.empty[ResourceLocation, DanCoreProgram]
  val resourceLocationEmpty = new ResourceLocation("")
  DanCoreRenderHelper.registerResourceReloadListener(this)

  def createShader(location: ResourceLocation, attributes: Seq[UniformBase]): DanCoreProgram = {
    try {
      val vertexLocation = new ResourceLocation(s"$location.vsh")
      val vertexId       = ShaderHelper.compileShader(vertexLocation, resourceManager, OpenGlHelper.GL_VERTEX_SHADER)

      val fragmentLocation = new ResourceLocation(s"$location.fsh")
      val fragmentId       = ShaderHelper.compileShader(fragmentLocation, resourceManager, OpenGlHelper.GL_FRAGMENT_SHADER)

      DanCoreProgram.create(vertexId, vertexLocation, fragmentId, fragmentLocation, attributes)
    } catch {
      case e: IOException =>
        if (location != resourceLocationEmpty) LogHelper.warn(s"Failed to load texture: $location", e)
        DanCoreProgram.MissingShader
      case NonFatal(throwable) =>
        val crashreport         = CrashReport.makeCrashReport(throwable, "Registering texture")
        val crashreportcategory = crashreport.makeCategory("Resource location being registered")
        crashreportcategory.addCrashSection("Resource location", location)
        throw new ReportedException(crashreport)
    }
  }

  def initShader(shaderLocation: ResourceLocation, attributes: Seq[UniformBase]): DanCoreProgram =
    shaderObjects.getOrElseUpdate(shaderLocation, createShader(shaderLocation, attributes))

  def getShader(shaderLocation: ResourceLocation): Option[DanCoreProgram] = shaderObjects.get(shaderLocation)

  def deleteShader(shaderLocation: ResourceLocation): Unit =
    shaderObjects.remove(shaderLocation).foreach { shader =>
      shader.delete()
    }

  override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {
    val bar = ProgressManager.push("Reloading Shader Manager", shaderObjects.size, true)

    val res = for ((resource, shader) <- shaderObjects) yield {
      bar.step(resource.toString)
      shader.delete()
      resource -> createShader(resource, shader.uniforms.map {
        case (name, DanCoreUniform(_, tpe, count, _, _)) => UniformBase(name, tpe, count)
      }.toSeq)
    }

    shaderObjects ++= res
    ProgressManager.pop(bar)
  }
}

@SideOnly(Side.CLIENT)
object ShaderManager extends ShaderManager(Minecraft.getMinecraft.getResourceManager)