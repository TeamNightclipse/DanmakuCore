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
import net.minecraft.client.renderer.{BufferBuilder, GlStateManager, Tessellator}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormPointedSphere extends FormGeneric(LibFormName.SPHERE_POINTED) {

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
      val tes   = Tessellator.getInstance
      val bb    = tes.getBuffer
      val shot  = danmaku.shotData
      val color = shot.getColor
      val sizeZ = shot.getSizeZ

      val centerZ1 = sizeZ * 1.2F / 2.0F
      val centerZ2 = sizeZ / 2.0F

      RenderHelper.transformEntity(danmaku)

      GL11.glTranslatef(0, 0, (-sizeZ / 6) * 4)

      createPointedSphere(tes, bb, 0xFFFFFF, 1F, centerZ1 - centerZ2, sizeZ)

      GlStateManager.scale(1.2F, 1.2F, 1.2F)
      GlStateManager.depthMask(false)
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)

      createPointedSphere(tes, bb, color, 0.6F, 0.0F, sizeZ)

      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }

    @SideOnly(Side.CLIENT)
    private def createPointedSphere(
        tes: Tessellator,
        vb: BufferBuilder,
        color: Int,
        alpha: Float,
        zPos: Float,
        sizeZ: Float
    ): Unit = {
      var usedZPos = zPos

      val r     = (color >> 16 & 255) / 255.0F
      val g     = (color >> 8 & 255) / 255.0F
      val b     = (color & 255) / 255.0F
      var width = 1F

      val resolutionXY = 8
      val resolutionZ  = 8

      val maxWidth = width

      var angleZ     = 0F
      val angleSpanZ = (Math.PI * 2.0D / resolutionXY).toFloat

      val segmentLengthZ = sizeZ / (resolutionZ - 1)
      var zPosOld        = usedZPos
      var xPos           = 0F
      var yPos           = 0F
      var xPos2          = 0F
      var yPos2          = 0F
      var xPosOld        = 0F
      var yPosOld        = 0F
      var xPos2Old       = 0F
      var yPos2Old       = 0F
      val angleSpan      = (Math.PI / resolutionZ).toFloat
      var angle          = ((-Math.PI / 2.0F) + angleSpan).toFloat
      var widthOld       = 0.0F

      for (_ <- 0 until resolutionZ) {
        usedZPos += segmentLengthZ
        width = MathHelper.cos(angle) * maxWidth
        xPosOld = MathHelper.cos(angleZ) * width
        yPosOld = MathHelper.sin(angleZ) * width
        xPos2Old = MathHelper.cos(angleZ) * widthOld
        yPos2Old = MathHelper.sin(angleZ) * widthOld
        angleZ = angleSpanZ

        for (_ <- 0 to resolutionXY) {
          xPos = MathHelper.cos(angleZ) * width
          yPos = MathHelper.sin(angleZ) * width
          xPos2 = MathHelper.cos(angleZ) * widthOld
          yPos2 = MathHelper.sin(angleZ) * widthOld

          vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
          vb.pos(xPosOld, yPosOld, usedZPos).color(r, g, b, alpha).endVertex()
          vb.pos(xPos2Old, yPos2Old, zPosOld).color(r, g, b, alpha).endVertex()
          vb.pos(xPos2, yPos2, zPosOld).color(r, g, b, alpha).endVertex()
          vb.pos(xPos, yPos, usedZPos).color(r, g, b, alpha).endVertex()
          tes.draw()

          xPosOld = xPos
          yPosOld = yPos
          xPos2Old = xPos2
          yPos2Old = yPos2
          angleZ += angleSpanZ
        }
        zPosOld = usedZPos
        angle += angleSpan
        widthOld = width
      }
    }
  }

  override def playShotSound(user: EntityLivingBase, shot: ShotData): Unit = {
    user.playSound(LibSounds.LASER2, 0.1F, 1F)
  }

  override def playShotSound(world: World, pos: Vector3, shot: ShotData): Unit = {
    DanmakuHelper.playSoundAt(world, pos, LibSounds.LASER2, 0.1F, 1F)
  }
}
