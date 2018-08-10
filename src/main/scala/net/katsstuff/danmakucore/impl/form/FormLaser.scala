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

import net.katsstuff.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.danmakucore.data.ShotData
import net.katsstuff.danmakucore.danmaku.form.{IRenderForm, RenderingProperty}
import net.katsstuff.danmakucore.lib.{LibFormName, LibSounds}
import net.katsstuff.danmakucore.scalastuff.DanmakuHelper
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import net.katsstuff.teamnightclipse.mirror.client.helper.MirrorRenderHelper
import net.katsstuff.teamnightclipse.mirror.client.shaders.MirrorShaderProgram
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}

private[danmakucore] class FormLaser extends FormGeneric(LibFormName.LASER) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm {

    @SideOnly(Side.CLIENT)
    override def renderLegacy(
        danmaku: DanmakuState,
        x: Double,
        y: Double,
        z: Double,
        orientation: Quat,
        partialTicks: Float,
        manager: RenderManager
    ): Unit = {
      val shot = danmaku.shot
      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      val dist = x * x + y * y + z * z
      if (shot.delay > 0) {
        val scale = 0.025F * Math.min(shot.delay, 20)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
        GlStateManager.depthMask(false)
        GlStateManager.scale(scale, scale, 1F)
        createCylinder(shot.edgeColor, 0.6F, dist)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
      } else {
        createCylinder(shot.coreColor, 1F, dist)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
        GlStateManager.depthMask(false)
        GlStateManager.scale(1.2F, 1.2F, 1.2F)
        createCylinder(shot.edgeColor, 0.3F, dist)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
      }
    }

    override def renderShaders(
        danmaku: DanmakuState,
        x: Double,
        y: Double,
        z: Double,
        orientation: Quat,
        partialTicks: Float,
        manager: RenderManager,
        shaderProgram: MirrorShaderProgram
    ): Unit = {

      val shot = danmaku.shot
      DanCoreRenderHelper.transformDanmaku(shot, orientation)
      DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, this, shot)

      val dist = x * x + y * y + z * z
      if (shot.delay > 0) {
        val scale = 0.025F * Math.min(shot.delay, 20)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)

        GlStateManager.scale(scale, scale, 1F)
        createCylinder(shot.edgeColor, 0.6F, dist)
        GlStateManager.translate(0F, 2F, 0F)

        GlStateManager.disableBlend()
      } else {
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        createCylinder(shot.edgeColor, 0.3F, dist)

        GlStateManager.disableBlend()
      }
    }

    private def createCylinder(color: Int, alpha: Float, dist: Double): Unit = {
      val cylinderLength = 0.5F
      val sphereSquish   = 0.005F
      //TODO: Use a half sphere connected to the cylinder here

      GL11.glPushMatrix()

      GL11.glTranslatef(0F, 0F, -cylinderLength)
      GlStateManager.scale(1F, 1F, sphereSquish)
      MirrorRenderHelper.drawSphere(color, alpha, dist)
      GlStateManager.scale(1F, 1F, 1F / sphereSquish)

      GL11.glTranslatef(0F, 0F, cylinderLength)
      MirrorRenderHelper.drawCylinder(color, alpha, dist)

      GL11.glTranslatef(0F, 0F, cylinderLength)
      GlStateManager.scale(1F, 1F, sphereSquish)
      MirrorRenderHelper.drawSphere(color, alpha, dist)
      GlStateManager.scale(1F, 1F, 1F / sphereSquish)

      GL11.glPopMatrix()
    }

    override def shader(state: DanmakuState): ResourceLocation =
      if (state.shot.delay > 0) DanCoreRenderHelper.baseDanmakuShaderLoc else DanCoreRenderHelper.fancyDanmakuShaderLoc

    override val defaultAttributeValues: Map[String, RenderingProperty] = Map(
      "coreSize"     -> RenderingProperty(1.1F, 0.5F, 10F),
      "coreHardness" -> RenderingProperty(2.5F, 0.5F, 10F),
      "edgeHardness" -> RenderingProperty(3F, 0.5F, 10F),
      "edgeGlow"     -> RenderingProperty(3F, 0.5F, 10F)
    )
  }

  override def playShotSound(user: EntityLivingBase, shotData: ShotData): Unit = ()

  override def playShotSound(world: World, pos: Vector3, shotData: ShotData): Unit = ()

  override def onTick(danmaku: DanmakuState): DanmakuUpdate =
    //The danmaku exits delay here
    super.onTick(danmaku).addCallbackIf(!danmaku.world.isRemote && danmaku.ticksExisted == 2) {
      DanmakuHelper.playSoundAt(danmaku.world, danmaku.pos, LibSounds.LASER1, 0.1F, 1F)
    }
}
