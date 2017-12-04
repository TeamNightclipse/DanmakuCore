/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network.scalachannel

import net.katsstuff.danmakucore.data.Vector3
import net.minecraft.entity.Entity
import net.minecraftforge.fml.common.network.NetworkRegistry

case class TargetPoint(dimension: Int, x: Double, y: Double, z: Double, range: Double) {
  def toMinecraft: NetworkRegistry.TargetPoint = new NetworkRegistry.TargetPoint(dimension, x, y, z, range)
}
object TargetPoint {
  def around(entity: Entity, range: Double): TargetPoint =
    TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range)

  def around(dimension: Int, pos: Vector3, range: Double): TargetPoint =
    TargetPoint(dimension, pos.x, pos.y, pos.z, range)
}
