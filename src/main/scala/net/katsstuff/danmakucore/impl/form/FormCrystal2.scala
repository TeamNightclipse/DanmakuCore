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

import net.katsstuff.danmakucore.lib.LibFormName
import net.katsstuff.mirror.client.helper.MirrorRenderHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * A One sided crystal. Pointy on one side.
  */
private[danmakucore] class FormCrystal2 extends AbstractFormCrystal(LibFormName.CRYSTAL2) {

  @SideOnly(Side.CLIENT)
  override protected def createCrystal(color: Int, alpha: Float, dist: Double): Unit = {
    GL11.glPushMatrix()

    GL11.glTranslatef(0F, 0F, 1F)
    MirrorRenderHelper.drawCone(color, alpha, dist)
    GL11.glTranslatef(0F, 0F, -1F)
    MirrorRenderHelper.drawCylinder(color, alpha, dist)
    GL11.glTranslatef(0F, 0F, -0.5F)
    MirrorRenderHelper.drawDisk(color, alpha, dist)

    GL11.glPopMatrix()
  }
}
