/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.form

import scala.util.Random

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.teamnightclipse.danmakucore.lib.LibFormName
import net.katsstuff.teamnightclipse.mirror.client.particles.GlowTexture
import net.katsstuff.teamnightclipse.mirror.data.Vector3

private[danmakucore] class FormFire extends FormSphere(LibFormName.FIRE) {

  override def onTick(danmaku: DanmakuState): DanmakuUpdate = {
    DanmakuUpdate.noUpdates(danmaku).addCallbackIf(danmaku.world.isRemote) {
      val shot  = danmaku.shot
      val color = shot.edgeColor
      val r     = Math.max(0.05F, (color >> 16 & 255) / 255.0F)
      val g     = Math.max(0.05F, (color >> 8 & 255) / 255.0F)
      val b     = Math.max(0.05F, (color & 255) / 255.0F)
      val size  = (shot.sizeX + shot.sizeY + shot.sizeZ) / 3

      val extraY = shot.sizeY / 2
      val diff   = danmaku.pos.add(0D, extraY, 0D) - danmaku.prevPos.add(0D, extraY, 0D)

      for (coeff <- 0D to 1D by 1 / 15D) {
        val pos = danmaku.prevPos.add(0D, extraY, 0D) + diff * coeff
        val motion = Vector3(
          0.0125f * (Random.nextFloat - 0.5f),
          0.0125f * (Random.nextFloat - 0.5f),
          0.0125f * (Random.nextFloat - 0.5f)
        )

        DanmakuCore.proxy.createParticleGlow(danmaku.world, pos, motion, r, g, b, size * 15F, 10, GlowTexture.MOTE)
      }
    }
  }
}
