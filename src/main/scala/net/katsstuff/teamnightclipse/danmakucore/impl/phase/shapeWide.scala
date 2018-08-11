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
import net.katsstuff.teamnightclipse.danmakucore.impl.shape.ShapeWide
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{PhaseManager, PhaseType}
import net.minecraft.nbt.NBTTagCompound

class PhaseTypeShapeWide extends PhaseType {
  override def instantiate(phaseManager: PhaseManager) =
    new PhaseWide(
      this,
      phaseManager,
      8,
      30F,
      0F,
      0.5D,
      ShotData.DefaultShotData,
      MovementData.constant(0.4D),
      RotationData.none
    )

  def instantiate(
      phaseManager: PhaseManager,
      amount: Int,
      wideAngle: Float,
      baseAngle: Float,
      distance: Double,
      shotData: ShotData,
      movementData: MovementData,
      rotationData: RotationData
  ) = new PhaseWide(this, phaseManager, amount, wideAngle, baseAngle, distance, shotData, movementData, rotationData)
}

private[phase] class PhaseWide(
    val phaseType: PhaseTypeShapeWide,
    manager: PhaseManager,
    amount: Int,
    var wideAngle: Float,
    baseAngle: Float,
    distance: Double,
    shotData: ShotData,
    movementData: MovementData,
    rotationData: RotationData
) extends PhaseShapeAbstract(manager, amount, baseAngle, distance, shotData, movementData, rotationData) {

  private val NbtWideAngle = "wideAngle"

  var shape: ShapeWide = {
    val template: DanmakuTemplate = DanmakuTemplate.builder
      .setUser(getEntity)
      .setShot(shotData)
      .setMovementData(movementData)
      .setRotationData(rotationData)
      .build

    new ShapeWide(template, amount, wideAngle, baseAngle, distance)
  }

  override def serializeNBT: NBTTagCompound = {
    val tag = super.serializeNBT
    tag.setFloat(NbtWideAngle, wideAngle)
    tag
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    super.deserializeNBT(tag)
    wideAngle = tag.getFloat(NbtWideAngle)

    val template = DanmakuTemplate.builder
      .setUser(getEntity)
      .setShot(shotData)
      .setMovementData(movementData)
      .setRotationData(rotationData)
      .build
    shape = new ShapeWide(template, amount, wideAngle, baseAngle, distance)
  }
}
