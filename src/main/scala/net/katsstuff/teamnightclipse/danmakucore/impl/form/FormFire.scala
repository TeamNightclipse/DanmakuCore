/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
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
