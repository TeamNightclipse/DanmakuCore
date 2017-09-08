/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.data

import scala.beans.{BeanProperty, BooleanBeanProperty}

import net.minecraft.nbt.{NBTTagCompound, NBTTagDouble, NBTTagList}
import net.minecraftforge.common.util.Constants

/**
	* If a [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]] rotates, defines how.
	*/
abstract sealed class AbstractRotationData {

  /**
		* If rotation is enabled
		*/
  def enabled: Boolean

  /**
		* How long the rotation will last in ticks
		*/
  def endTime: Int

  /**
		* The rotation quat itself. Does most of the work.
		*/
  def rotationQuat: Quat

  def serializeNBT: NBTTagCompound = {
    val tag  = new NBTTagCompound
    val list = new NBTTagList
    list.appendTag(new NBTTagDouble(rotationQuat.x))
    list.appendTag(new NBTTagDouble(rotationQuat.y))
    list.appendTag(new NBTTagDouble(rotationQuat.z))
    list.appendTag(new NBTTagDouble(rotationQuat.w))
    tag.setTag(RotationData.NbtRotation, list)
    tag.setInteger(RotationData.NbtRotationEnd, endTime)
    tag.setBoolean(RotationData.NbtRotationEnabled, enabled)
    tag
  }
}

final case class MutableRotationData(
    @BooleanBeanProperty var enabled: Boolean,
    @BeanProperty var rotationQuat: Quat,
    @BeanProperty var endTime: Int
) extends AbstractRotationData {

  def setRotationVec(vector: Vector3): MutableRotationData = {
    val angle = rotationQuat.computeAngle
    rotationQuat = Quat.fromAxisAngle(vector, angle)
    this
  }

  def setRotationAngle(angle: Double): MutableRotationData = {
    val vector = rotationQuat.computeVector
    rotationQuat = Quat.fromAxisAngle(vector, angle)
    this
  }

  def copyObj: MutableRotationData = copy()
}
final case class RotationData(
    @BooleanBeanProperty enabled: Boolean,
    @BeanProperty rotationQuat: Quat,
    @BeanProperty endTime: Int
) extends AbstractRotationData {

  def setEnabled(enabled: Boolean):        RotationData = copy(enabled = enabled)
  def setRotationQuat(rotationQuat: Quat): RotationData = copy(rotationQuat = rotationQuat)
  def setEndTime(endTime: Int):            RotationData = copy(endTime = endTime)

  def setRotationVec(vector: Vector3): RotationData = {
    val angle = rotationQuat.computeAngle
    copy(rotationQuat = Quat.fromAxisAngle(vector, angle))
  }

  def setRotationAngle(angle: Double): RotationData = {
    val vector = rotationQuat.computeVector
    copy(rotationQuat = Quat.fromAxisAngle(vector, angle))
  }
}

object RotationData {

  final val NbtRotation        = "rotation"
  final val NbtRotationEnd     = "end"
  final val NbtRotationEnabled = "enabled"

  val none = RotationData(enabled = false, Quat.Identity, 9999)

  def fromNBT(tag: NBTTagCompound): RotationData = {
    val list     = tag.getTagList(NbtRotation, Constants.NBT.TAG_DOUBLE)
    val rotation = Quat(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2), list.getDoubleAt(3))
    val endTime  = tag.getInteger(NbtRotationEnd)
    val enabled  = tag.getBoolean(NbtRotationEnabled)
    RotationData(enabled, rotation, endTime)
  }
}
