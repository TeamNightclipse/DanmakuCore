/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form

import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.danmaku.form.IRenderForm
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

import net.katsstuff.mirror.data.Quat

abstract private[danmakucore] class AbstractFormCrystal(name: String) extends FormGeneric(name) {

  //noinspection ConvertExpressionToSAM
  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {

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
      val shot  = danmaku.shot
      val sizeX = shot.sizeX
      val sizeY = shot.sizeY
      val sizeZ = shot.sizeZ
      val dist  = x * x + y * y + z * z

      GlStateManager.rotate(orientation.toQuaternion)
      GL11.glScalef((sizeX / 3) * 2, (sizeY / 3) * 2, sizeZ)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)

      createCrystal(shot.edgeColor, 0.3F, dist)

      GlStateManager.depthMask(true)
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      GlStateManager.disableBlend()

      GlStateManager.scale(1 / 1.2F, 1 / 1.2F, 1 / 1.2F)

      createCrystal(shot.coreColor, 1F, dist)
    }
  }

  @SideOnly(Side.CLIENT)
  protected def createCrystal(color: Int, alpha: Float, dist: Double): Unit
}
