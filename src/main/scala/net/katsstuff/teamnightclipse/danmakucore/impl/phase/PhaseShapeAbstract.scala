/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
