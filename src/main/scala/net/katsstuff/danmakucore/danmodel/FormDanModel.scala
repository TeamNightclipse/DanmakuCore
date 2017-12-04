/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmodel

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.impl.form.FormGeneric
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

class FormDanModel(name: String, model: DanModel) extends FormGeneric(name) {
  @SideOnly(Side.CLIENT)
  override protected def createRenderer(): IRenderForm = {
    (
        danmaku: EntityDanmaku,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float,
        man: RenderManager
    ) =>
      val tes      = Tessellator.getInstance
      val vb       = tes.getBuffer
      val pitch    = danmaku.rotationPitch
      val yaw      = danmaku.rotationYaw
      val roll     = danmaku.roll
      val shotData = danmaku.getShotData
      val sizeX    = shotData.getSizeX
      val sizeY    = shotData.getSizeY
      val sizeZ    = shotData.getSizeZ
      val color    = shotData.getColor

      GL11.glRotatef(-yaw, 0F, 1F, 0F)
      GL11.glRotatef(pitch, 1F, 0F, 0F)
      GL11.glRotatef(roll, 0F, 0F, 1F)
      GL11.glScalef(sizeX, sizeY, sizeZ)

      model.render(vb, tes, color)
  }
}
