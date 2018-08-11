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
package net.katsstuff.teamnightclipse.danmakucore.impl.phase

import net.katsstuff.teamnightclipse.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.Phase
import net.katsstuff.teamnightclipse.danmakucore.shape.Shape
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{Phase, PhaseManager}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.minecraft.nbt.NBTTagCompound

abstract class PhaseShapeAbstract(
    manager: PhaseManager,
    var amount: Int,
    var baseAngle: Float,
    var distance: Double,
    var shotData: ShotData,
    var movementData: MovementData,
    var rotationData: RotationData
) extends Phase(manager) {

  private val NbtAmount       = "amount"
  private val NbtBaseAngle    = "baseAngle"
  private val NbtDistance     = "distance"
  private val NbtShotData     = "shotData"
  private val NbtMovementData = "movementData"
  private val NbtRotationData = "rotationData"

  def shape: Shape

  override def init(): Unit = {
    super.init()
    interval = 5
  }

  override def serverUpdate(): Unit = {
    super.serverUpdate()

    val entity = getEntity
    val target = entity.getAttackTarget

    if (!isFrozen && isCounterStart && target != null && entity.getEntitySenses.canSee(target)) {
      val entityPos = new Vector3(entity)
      val forward   = Vector3.directionToEntity(entityPos, target)

      shape.draw(entityPos, Quat.lookRotation(forward, Vector3.Up), 0)
    }
  }

  override def serializeNBT: NBTTagCompound = {
    val tag = super.serializeNBT
    tag.setInteger(NbtAmount, amount)
    tag.setFloat(NbtBaseAngle, baseAngle)
    tag.setDouble(NbtDistance, distance)
    tag.setTag(NbtShotData, shotData.serializeNBT)
    tag.setTag(NbtMovementData, movementData.serializeNBT)
    tag.setTag(NbtRotationData, rotationData.serializeNBT)
    tag
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    super.deserializeNBT(tag)
    amount = tag.getInteger(NbtAmount)
    baseAngle = tag.getFloat(NbtBaseAngle)
    distance = tag.getDouble(NbtDistance)
    shotData = new ShotData(tag.getCompoundTag(NbtShotData))
    movementData = MovementData.fromNBT(tag.getCompoundTag(NbtMovementData))
    rotationData = RotationData.fromNBT(tag.getCompoundTag(NbtRotationData))
  }
}
