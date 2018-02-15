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
import net.katsstuff.danmakucore.impl.shape.ShapeRing
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
