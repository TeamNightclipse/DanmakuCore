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

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuTemplate
import net.katsstuff.teamnightclipse.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.PhaseType
import net.katsstuff.teamnightclipse.danmakucore.impl.shape.ShapeRing
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{PhaseManager, PhaseType}
import net.minecraft.nbt.NBTTagCompound

class PhaseTypeShapeRing extends PhaseType {
  override def instantiate(phaseManager: PhaseManager) =
    new PhaseRing(
      this,
      phaseManager,
      8,
      2F,
      0F,
      0.5D,
      ShotData.DefaultShotData,
      MovementData.constant(0.4D),
      RotationData.none
    )
  def instantiate(
      phaseManager: PhaseManager,
      amount: Int,
      radius: Float,
      baseAngle: Float,
      distance: Double,
      shotData: ShotData,
      movementData: MovementData,
      rotationData: RotationData
  ) =
    new PhaseRing(
      this,
      phaseManager,
      amount,
      radius,
      baseAngle,
      distance,
      shotData,
      movementData,
      rotationData
    )
}

private[phase] class PhaseRing(
    val phaseType: PhaseTypeShapeRing,
    manager: PhaseManager,
    amount: Int,
    var radius: Float,
    baseAngle: Float,
    distance: Double,
    shotData: ShotData,
    movementData: MovementData,
    rotationData: RotationData
) extends PhaseShapeAbstract(manager, amount, baseAngle, distance, shotData, movementData, rotationData) {

  private val NbtRadius = "radius"

  var shape: ShapeRing = {
    val template: DanmakuTemplate = DanmakuTemplate.builder
      .setUser(getEntity)
      .setShot(shotData)
      .setMovementData(movementData)
      .setRotationData(rotationData)
      .build

    new ShapeRing(template, amount, radius, baseAngle, distance)
  }
  override def serializeNBT: NBTTagCompound = {
    val tag = super.serializeNBT
    tag.setFloat(NbtRadius, radius)
    tag
  }
  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    super.deserializeNBT(tag)
    radius = tag.getFloat(NbtRadius)
    val template = DanmakuTemplate.builder
      .setUser(getEntity)
      .setShot(shotData)
      .setMovementData(movementData)
      .setRotationData(rotationData)
      .build
    shape = new ShapeRing(template, amount, radius, baseAngle, distance)
  }
}
