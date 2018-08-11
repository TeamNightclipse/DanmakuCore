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
import net.katsstuff.teamnightclipse.danmakucore.impl.shape.ShapeCircle
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{PhaseManager, PhaseType}
import net.minecraft.nbt.NBTTagCompound

class PhaseTypeShapeCircle extends PhaseType {
  override def instantiate(phaseManager: PhaseManager) =
    new PhaseCircle(
      this,
      phaseManager,
      8,
      0F,
      0.5D,
      ShotData.DefaultShotData,
      MovementData.constant(0.4D),
      RotationData.none
    )
  def instantiate(
      phaseManager: PhaseManager,
      amount: Int,
      baseAngle: Float,
      distance: Double,
      shotData: ShotData,
      movementData: MovementData,
      rotationData: RotationData
  ) = new PhaseCircle(this, phaseManager, amount, baseAngle, distance, shotData, movementData, rotationData)
}

private[phase] class PhaseCircle(
    val phaseType: PhaseTypeShapeCircle,
    manager: PhaseManager,
    amount: Int,
    baseAngle: Float,
    distance: Double,
    shotData: ShotData,
    movementData: MovementData,
    rotationData: RotationData
) extends PhaseShapeAbstract(manager, amount, baseAngle, distance, shotData, movementData, rotationData) {

  var shape: ShapeCircle = {
    val template: DanmakuTemplate = DanmakuTemplate.builder
      .setUser(getEntity)
      .setShot(shotData)
      .setMovementData(movementData)
      .setRotationData(rotationData)
      .build

    new ShapeCircle(template, amount, baseAngle, distance)
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    super.deserializeNBT(tag)
    val template = DanmakuTemplate.builder
      .setUser(getEntity)
      .setShot(shotData)
      .setMovementData(movementData)
      .setRotationData(rotationData)
      .build
    shape = new ShapeCircle(template, amount, baseAngle, distance)
  }
}
