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
package net.katsstuff.teamnightclipse.danmakucore.data

import scala.beans.{BeanProperty, BooleanBeanProperty}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.minecraft.nbt.{NBTTagCompound, NBTTagDouble, NBTTagList}
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.util.Constants

/**
	* If a [[net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState]] rotates, defines how.
	*/
sealed abstract class AbstractRotationData {

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

  /**
    * The local space point where the rotation should take place around
    */
  def pivot: Vector3

  def serializeNBT: NBTTagCompound = {
    val tag  = new NBTTagCompound
    val rotList = new NBTTagList
    rotList.appendTag(new NBTTagDouble(rotationQuat.x))
    rotList.appendTag(new NBTTagDouble(rotationQuat.y))
    rotList.appendTag(new NBTTagDouble(rotationQuat.z))
    rotList.appendTag(new NBTTagDouble(rotationQuat.w))
    tag.setTag(RotationData.NbtRotation, rotList)
    tag.setInteger(RotationData.NbtRotationEnd, endTime)
    tag.setBoolean(RotationData.NbtRotationEnabled, enabled)
    val pivotList = new NBTTagList
    pivotList.appendTag(new NBTTagDouble(pivot.x))
    pivotList.appendTag(new NBTTagDouble(pivot.y))
    pivotList.appendTag(new NBTTagDouble(pivot.z))
    tag.setTag(RotationData.NbtRotationPivot, pivotList)
    tag
  }
}

final case class MutableRotationData(
    @BooleanBeanProperty var enabled: Boolean,
    @BeanProperty var rotationQuat: Quat,
    @BeanProperty var endTime: Int,
    @BeanProperty var pivot: Vector3
) extends AbstractRotationData {

  @deprecated("Use the one that also includes the pivot", since = "0.8.0")
  def this(enabled: Boolean, rotationQuat: Quat, endTime: Int) = this(enabled, rotationQuat, endTime, Vector3.Zero)

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
object MutableRotationData {

  @deprecated("Use the one that also includes the pivot", since = "0.8.0")
  def apply(enabled: Boolean, rotationQuat: Quat, endTime: Int): MutableRotationData =
    new MutableRotationData(enabled, rotationQuat, endTime, Vector3.Zero)
}
final case class RotationData(
    @BooleanBeanProperty enabled: Boolean,
    @BeanProperty rotationQuat: Quat,
    @BeanProperty endTime: Int,
    @BeanProperty pivot: Vector3
) extends AbstractRotationData {

  @deprecated("Use the one that also includes the pivot", since = "0.8.0")
  def this(enabled: Boolean, rotationQuat: Quat, endTime: Int) = this(enabled, rotationQuat, endTime, Vector3.Zero)

  def setEnabled(enabled: Boolean): RotationData        = copy(enabled = enabled)
  def setRotationQuat(rotationQuat: Quat): RotationData = copy(rotationQuat = rotationQuat)
  def setEndTime(endTime: Int): RotationData            = copy(endTime = endTime)

  def setRotationVec(vector: Vector3): RotationData = {
    val angle = rotationQuat.computeAngle
    copy(rotationQuat = Quat.fromAxisAngle(vector, angle))
  }

  def setRotationAngle(angle: Double): RotationData = {
    val vector = rotationQuat.computeVector
    copy(rotationQuat = Quat.fromAxisAngle(vector, angle))
  }

  def setPivot(pivot: Vector3): RotationData = copy(pivot = pivot)
}

object RotationData {

  @deprecated("Use the one that also includes the pivot", since = "0.8.0")
  def apply(enabled: Boolean, rotationQuat: Quat, endTime: Int): RotationData =
    new RotationData(enabled, rotationQuat, endTime, Vector3.Zero)

  final val NbtRotation        = "rotation"
  final val NbtRotationEnd     = "end"
  final val NbtRotationEnabled = "enabled"
  final val NbtRotationPivot   = "pivot"

  val none = RotationData(enabled = false, Quat.Identity, 9999, Vector3.Zero)

  def fromNBT(tag: NBTTagCompound): RotationData = {
    val rotList   = tag.getTagList(NbtRotation, Constants.NBT.TAG_DOUBLE)
    val rotation  = Quat(rotList.getDoubleAt(0), rotList.getDoubleAt(1), rotList.getDoubleAt(2), rotList.getDoubleAt(3))
    val endTime   = tag.getInteger(NbtRotationEnd)
    val enabled   = tag.getBoolean(NbtRotationEnabled)
    val pivotList = tag.getTagList(NbtRotationPivot, Constants.NBT.TAG_DOUBLE)
    val pivot     = Vector3(pivotList.getDoubleAt(0), pivotList.getDoubleAt(1), pivotList.getDoubleAt(2))
    RotationData(enabled, rotation, endTime, pivot)
  }

  def fromEulerLocal(yaw: Float, pitch: Float, roll: Float, orientation: Quat): Quat = {
    val clampedPitch = if (pitch > 90F || pitch < -90F) Math.IEEEremainder(pitch, 180F) else pitch
    val clampedYaw   = if (yaw > 180F || yaw < -180F) Math.IEEEremainder(yaw, 360F) else yaw
    val clampedRoll  = if (roll > 180F || roll < -180F) Math.IEEEremainder(roll, 360F) else roll

    fromEulerRadLocal(
      Math.toRadians(clampedYaw).toFloat,
      Math.toRadians(clampedPitch).toFloat,
      Math.toRadians(clampedRoll).toFloat,
      orientation
    )
  }

  //TODO: Move this to Mirror at some point
  def fromEulerRadLocal(yaw: Float, pitch: Float, roll: Float, orientation: Quat): Quat = {
    val cy = MathHelper.cos(yaw / 2)
    val cp = MathHelper.cos(pitch / 2)
    val cr = MathHelper.cos(roll / 2)

    val sy = MathHelper.sin(yaw / 2)
    val sp = MathHelper.sin(pitch / 2)
    val sr = MathHelper.sin(roll / 2)

    val u = orientation * Vector3.Up
    val r = orientation * Vector3.Right
    val f = orientation * Vector3.Forward

    val yx = sy * u.x
    val yy = sy * u.y
    val yz = sy * u.z
    val yw = cy

    val px = sp * r.x
    val py = sp * r.y
    val pz = sp * r.z
    val pw = cp

    val rx = sr * f.x
    val ry = sr * f.y
    val rz = sr * f.z
    val rw = cr

    val ypx = yw * px + yx * pw + yy * pz - yz * py
    val ypy = yw * py + yy * pw + yz * px - yx * pz
    val ypz = yw * pz + yz * pw + yx * py - yy * px
    val ypw = yw * pw - yx * px - yy * py - yz * pz

    val yprx = ypw * rx + ypx * rw + ypy * rz - ypz * ry
    val ypry = ypw * ry + ypy * rw + ypz * rx - ypx * rz
    val yprz = ypw * rz + ypz * rw + ypx * ry - ypy * rx
    val yprw = ypw * rw - ypx * rx - ypy * ry - ypz * rz

    Quat(yprx, ypry, yprz, yprw)
  }
}
