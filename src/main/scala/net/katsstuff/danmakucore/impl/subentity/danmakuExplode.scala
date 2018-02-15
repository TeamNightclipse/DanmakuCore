/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity

import net.katsstuff.danmakucore.danmaku.{DanmakuState, DanmakuTemplate, DanmakuUpdate}
import net.katsstuff.danmakucore.data.{Quat, Vector3}
import net.katsstuff.danmakucore.danmaku.subentity.SubEntityType
import net.katsstuff.danmakucore.lib.data.LibSubEntities
import net.katsstuff.danmakucore.scalastuff.DanmakuCreationHelper
import net.minecraft.util.math.RayTraceResult

private[danmakucore] class SubEntityTypeDanmakuExplosion(name: String) extends SubEntityType(name) {
  override def instantiate = new SubEntityDanmakuExplosion
}

private[subentity] class SubEntityDanmakuExplosion extends SubEntityDefault {

  override protected def impact(danmaku: DanmakuState, raytrace: RayTraceResult): Option[DanmakuUpdate] = {
    if (!danmaku.world.isRemote) {
      super.impact(danmaku, raytrace).map(_.addCallback(createSphere(danmaku)))
    } else {
      super.impact(danmaku, raytrace)
    }
  }

  override def subEntityTick(danmaku: DanmakuState): Option[DanmakuUpdate] = {
    if (danmaku.isShotEndTime && !danmaku.world.isRemote) {
      super.subEntityTick(danmaku).map(_.addCallback(createSphere(danmaku)))
    } else {
      super.subEntityTick(danmaku)
    }
  }

  def createSphere(danmaku: DanmakuState): () => Unit = () => {
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

    //DanmakuCreationHelper.createSphereShot(template, 16, 16, 0F, 0D)
  }
}
