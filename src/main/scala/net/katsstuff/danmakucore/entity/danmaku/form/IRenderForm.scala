/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * A interface used to render danmaku forms.
  * Only exists on the client to be safe.
  */
@SideOnly(Side.CLIENT)
trait IRenderForm {

  /**
    * Do your rendering like normal in here. Note that the texture is already applied, lighting is
    * disabled, and the entity is translated to it's position. You do not need to call glPushMatrix
    * or glPopMatrix.
    */
  def renderForm(
      danmaku: EntityDanmaku,
      x: Double,
      y: Double,
      z: Double,
      entityYaw: Float,
      partialTicks: Float,
      rendermanager: RenderManager
  ): Unit
}
