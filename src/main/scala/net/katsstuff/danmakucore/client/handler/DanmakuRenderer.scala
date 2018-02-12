/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler

import scala.collection.mutable

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.client.shader.{DanCoreShaderProgram, ShaderManager}
import net.katsstuff.danmakucore.danmaku.{DanmakuHandler, DanmakuState}
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.Form
import net.katsstuff.danmakucore.helper.LogHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.{GlStateManager, OpenGlHelper, RenderGlobal}
import net.minecraft.client.resources.I18n
import net.minecraftforge.client.event.{RenderGameOverlayEvent, RenderWorldLastEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class DanmakuRenderer(handler: DanmakuHandler) {

  final private val invalidForms = new mutable.HashSet[Form]
  private val mc                 = Minecraft.getMinecraft
  private val useShaders         = OpenGlHelper.shadersSupported

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  def onDebugInfo(event: RenderGameOverlayEvent.Text): Unit = {
    if(mc.gameSettings.showDebugInfo) {
      event.getLeft.add(s"Danmaku count: ${handler.allDanmaku.size}")
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  def onRenderAfterWorld(event: RenderWorldLastEvent): Unit = {
    GlStateManager.pushMatrix()
    GlStateManager.disableLighting() //Lighting is disabled for rendering danmaku we disable it here instead
    val partialTicks  = event.getPartialTicks
    val renderManager = mc.getRenderManager

    val entity     = mc.getRenderViewEntity
    val renderPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks
    val renderPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks
    val renderPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks

    if (useShaders) {
      val danmakuByShader = handler.allDanmaku
        .groupBy(s => s.shot.form.getRenderer(s).shader(s))
        .flatMap {
          case (rl, danmaku) =>
            ShaderManager.getShaderProgram(rl).map(_ -> danmaku)
        }

      danmakuByShader.foreach {
        case (shader, allDanmaku) =>
          shader.begin()
          allDanmaku.foreach { danmaku =>
            preRenderDanmaku(danmaku, partialTicks, renderPosX, renderPosY, renderPosZ, renderManager, Some(shader))
          }
          shader.end()
      }
    } else {
      handler.allDanmaku.foreach { danmaku =>
        preRenderDanmaku(danmaku, partialTicks, renderPosX, renderPosY, renderPosZ, renderManager, None)
      }
    }

    GlStateManager.enableLighting()
    GlStateManager.popMatrix()
  }

  def preRenderDanmaku(
      danmaku: DanmakuState,
      partialTicks: Float,
      renderPosX: Double,
      renderPosY: Double,
      renderPosZ: Double,
      renderManager: RenderManager,
      shader: Option[DanCoreShaderProgram]
  ): Unit = {
    val x           = danmaku.prevPos.x + (danmaku.pos.x - danmaku.prevPos.x) * partialTicks
    val y           = danmaku.prevPos.y + (danmaku.pos.y - danmaku.prevPos.y) * partialTicks
    val z           = danmaku.prevPos.z + (danmaku.pos.z - danmaku.prevPos.z) * partialTicks
    val orientation = danmaku.prevOrientation.slerp(danmaku.orientation, partialTicks)
    val i           = danmaku.renderBrightness

    val j = i % 65536
    val k = i / 65536
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k)
    GlStateManager.color(1F, 1F, 1F, 1F)
    renderDanmaku(
      danmaku,
      x - renderPosX,
      y - renderPosY,
      z - renderPosZ,
      orientation,
      partialTicks,
      renderManager,
      shader
    )
  }

  def renderDanmaku(
      danmaku: DanmakuState,
      x: Double,
      y: Double,
      z: Double,
      orientation: Quat,
      partialTicks: Float,
      renderManager: RenderManager,
      optShader: Option[DanCoreShaderProgram]
  ): Unit = {
    val shot = danmaku.shot

    //We don't want to render expired danmaku
    if (danmaku.ticksExisted <= shot.end) {
      GL11.glPushMatrix()
      renderManager.renderEngine.bindTexture(shot.form.getTexture(danmaku))
      GL11.glTranslated(x, y + shot.sizeY / 2, z)

      val form       = shot.form
      val renderForm = form.getRenderer(danmaku)
      if (renderForm != null) {
        optShader match {
          case Some(shader) =>
            renderForm.renderShaders(danmaku, x, y, z, orientation, partialTicks, renderManager, shader)
          case None => renderForm.renderLegacy(danmaku, x, y, z, orientation, partialTicks, renderManager)
        }
      } else if (!invalidForms.contains(form)) {
        LogHelper.error("Invalid renderer for " + I18n.format(form.unlocalizedName))
        invalidForms.add(form)
      }

      GL11.glPopMatrix()

      //From RenderManager renderDebugBoundingBox
      if (renderManager.isDebugBoundingBox && !mc.isReducedDebug) {
        GlStateManager.pushMatrix()
        GlStateManager.depthMask(false)
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.disableBlend()
        GL11.glTranslated(x, y + shot.sizeY / 2, z)

        danmaku.boundingBoxes.foreach { obb =>
          val aabb = obb.aabb

          GlStateManager.pushMatrix()
          GlStateManager.rotate(obb.orientation.toQuaternion)
          RenderGlobal.drawSelectionBoundingBox(
            aabb.offset(-danmaku.pos.x, -danmaku.pos.y, -danmaku.pos.z),
            0F,
            1F,
            0F,
            1F
          )
          GlStateManager.popMatrix()
        }

        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()
        GlStateManager.enableCull()
        GlStateManager.disableBlend()
        GlStateManager.depthMask(true)
        GlStateManager.popMatrix()
      }
    }
  }
}
