/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.render

import scala.collection.mutable

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.Form
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.client.renderer.{GlStateManager, RenderGlobal}
import net.minecraft.util.ResourceLocation

class RenderDanmaku(val renderManagerIn: RenderManager) extends Render[EntityDanmaku](renderManagerIn) {
  final private val invalidForms = new mutable.HashSet[Form]
  override def doRender(
      entity: EntityDanmaku,
      x: Double,
      y: Double,
      z: Double,
      entityYaw: Float,
      partialTicks: Float
  ): Unit = {
    val shotData = entity.getShotData
    //We don't want to render expired danmaku
    if (entity.ticksExisted <= shotData.end) {
      GL11.glPushMatrix()
      bindEntityTexture(entity)
      GL11.glTranslated(x, y + shotData.sizeY / 2, z)
      GlStateManager.disableLighting()

      /*
      val form       = shotData.form
      val renderForm = form.getRenderer(entity)
      if (renderForm != null) renderForm.renderForm(entity, x, y, z, entityYaw, partialTicks, renderManager)
      else if (!invalidForms.contains(form)) {
        LogHelper.error("Invalid renderer for " + I18n.format(form.unlocalizedName))
        invalidForms.add(form)
      }
      */

      GlStateManager.enableLighting()
      GL11.glPopMatrix()

      //From RenderManager renderDebugBoundingBox
      if (renderManager.isDebugBoundingBox && !entity.isInvisible && !Minecraft.getMinecraft.isReducedDebug) {
        GlStateManager.pushMatrix()
        GlStateManager.depthMask(false)
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.disableBlend()
        GL11.glTranslated(x, y + shotData.sizeY / 2, z)

        val obb  = entity.getOrientedBoundingBox
        val aabb = obb.aabb

        GlStateManager.rotate(obb.orientation.toQuaternion)
        RenderGlobal.drawSelectionBoundingBox(aabb.offset(-entity.posX, -entity.posY, -entity.posZ), 0F, 1F, 0F, 1F)

        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()
        GlStateManager.enableCull()
        GlStateManager.disableBlend()
        GlStateManager.depthMask(true)
        GlStateManager.popMatrix()
      }
      super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }
  }
  override protected def getEntityTexture(entity: EntityDanmaku): ResourceLocation = {
    //entity.getShotData.form.getTexture(entity)
    null
  }
}
