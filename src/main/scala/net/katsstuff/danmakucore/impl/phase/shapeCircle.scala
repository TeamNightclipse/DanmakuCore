/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase

import net.katsstuff.danmakucore.danmaku.DanmakuTemplate
import net.katsstuff.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.danmakucore.entity.living.phase.{PhaseManager, PhaseType}
import net.katsstuff.danmakucore.impl.shape.ShapeCircle
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
