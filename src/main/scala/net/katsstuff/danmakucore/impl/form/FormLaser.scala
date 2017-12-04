/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.client.helper.RenderHelper
import net.katsstuff.danmakucore.data.{ShotData, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.{LibFormName, LibSounds}
import net.katsstuff.danmakucore.scalastuff.DanmakuHelper
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.EntityLivingBase
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormLaser extends FormGeneric(LibFormName.LASER) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {
    @SideOnly(Side.CLIENT)
    override def renderForm(
        danmaku: EntityDanmaku,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float,
        rendermanager: RenderManager
    ): Unit = {
      val shotData = danmaku.shotData
      val color    = shotData.getColor

      RenderHelper.transformEntity(danmaku)

      if (shotData.delay > 0) {
        val scale = 0.025F * Math.min(shotData.delay, 20)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
        GlStateManager.depthMask(false)
        GlStateManager.scale(scale, scale, 1F)
        createCylinder(color, 0.6F)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
      } else {
        createCylinder(0xFFFFFF, 1F)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
        GlStateManager.depthMask(false)
        GlStateManager.scale(1.2F, 1.2F, 1.2F)
        createCylinder(color, 0.3F)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
      }
    }

    @SideOnly(Side.CLIENT)
    private def createCylinder(color: Int, alpha: Float): Unit = {
      GL11.glPushMatrix()
      GL11.glTranslatef(0F, 0F, -0.5F)
      RenderHelper.drawDisk(color, alpha)
      GL11.glTranslatef(0F, 0F, 0.5F)
      RenderHelper.drawCylinder(color, alpha)
      GL11.glTranslatef(0F, 0F, 0.5F)
      GL11.glRotatef(180, 0F, 1F, 0F)
      RenderHelper.drawDisk(color, alpha)
      GL11.glPopMatrix()
    }
  }

  override def playShotSound(user: EntityLivingBase, shotData: ShotData): Unit = ()

  override def playShotSound(world: World, pos: Vector3, shotData: ShotData): Unit = ()

  override def onTick(danmaku: EntityDanmaku): Unit = {
    //The danmaku exits delay here
    if (danmaku.ticksExisted == 2) {
      danmaku.playSound(LibSounds.LASER1, 0.1F, 1F)
    }
  }
}