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
package net.katsstuff.teamnightclipse.danmakucore.impl.subentity

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuTemplate, DanmakuUpdate}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.SubEntityType
import net.katsstuff.teamnightclipse.danmakucore.lib.data.LibSubEntities
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanmakuCreationHelper
import net.minecraft.util.math.RayTraceResult

private[danmakucore] class SubEntityTypeDanmakuExplosion(name: String) extends SubEntityType(name) {
  override def instantiate = new SubEntityDanmakuExplosion
}

private[subentity] class SubEntityDanmakuExplosion extends SubEntityDefault {

  override protected def impact(danmaku: DanmakuState, raytrace: RayTraceResult): DanmakuUpdate =
    super.impact(danmaku, raytrace).addCallbackIf(!danmaku.world.isRemote)(createSphere(danmaku))

  override def subEntityTick(danmaku: DanmakuState): DanmakuUpdate =
    super.subEntityTick(danmaku).addCallbackIf(danmaku.isShotEndTime && !danmaku.world.isRemote)(createSphere(danmaku))

  def createSphere(danmaku: DanmakuState): Unit = {
    val template = DanmakuTemplate(
      danmaku.world,
      danmaku.user,
      danmaku.source,
      danmaku.shot.copy(subEntity = LibSubEntities.DEFAULT_TYPE),
      danmaku.pos,
      danmaku.direction,
      Quat.fromAxisAngle(Vector3.Forward, 0F),
      danmaku.movement,
      danmaku.rotation,
      danmaku.entity.rawBoundingBoxes
    )

    DanmakuCreationHelper.createSphereShot(template, 16, 16, 0F, 0D)
  }
}
