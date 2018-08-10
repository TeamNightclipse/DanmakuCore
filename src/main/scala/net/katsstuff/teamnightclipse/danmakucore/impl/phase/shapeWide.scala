/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
