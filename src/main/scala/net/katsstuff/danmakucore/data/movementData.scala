/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.data

import scala.beans.BeanProperty

import net.minecraft.nbt.{NBTTagCompound, NBTTagDouble, NBTTagList}
import net.minecraftforge.common.util.Constants

/**
	* Defines how a [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]] will move.
	*/
abstract sealed class AbstractMovementData {

	/**
		* The speed that the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]] starts with.
		* This can be higher than the [[speedLimit]]
		*/
	def speedOriginal: Double

	/**
		* The limit of the speed of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]].
		* Once it has reached this, it will not continue to change speed.
		*/
	def speedLimit: Double

	/**
		* The change in speed each tick. If this is negative, and
		* the [[speedLimit]] is lower than the [[speedOriginal]], the
		* [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]] will slow down,
		* if else it will speed up.
		*/
	def speedAcceleration: Double

	/**
		* The gravity that is applied each tick to the entity's speed.
		* Think of this as an additional, directional [[speedAcceleration]].
		*/
	def gravity: Vector3

	def serializeNBT: NBTTagCompound = {
		val tag = new NBTTagCompound
		tag.setDouble(MovementData.NbtOriginal, speedOriginal)
		tag.setDouble(MovementData.NbtLimit, speedLimit)
		tag.setDouble(MovementData.NbtAcceleration, speedAcceleration)

		val list = new NBTTagList
		list.appendTag(new NBTTagDouble(gravity.x))
		list.appendTag(new NBTTagDouble(gravity.y))
		list.appendTag(new NBTTagDouble(gravity.z))
		tag.setTag(MovementData.NbtGravity, list)
		tag
	}
}

final case class MutableMovementData(
	@BeanProperty var speedOriginal: Double,
	@BeanProperty var speedLimit: Double,
	@BeanProperty var speedAcceleration: Double,
	@BeanProperty var gravity: Vector3) extends AbstractMovementData {

	def copyObj: MutableMovementData = copy()
}

final case class MovementData(
	@BeanProperty speedOriginal: Double,
	@BeanProperty speedLimit: Double,
	@BeanProperty speedAcceleration: Double,
	@BeanProperty gravity: Vector3) extends AbstractMovementData {

	def setSpeedOriginal(speedOriginal: Double): MovementData = copy(speedOriginal = speedOriginal)
	def setSpeedLimit(speedLimit: Double): MovementData = copy(speedLimit = speedLimit)
	def setSpeedAcceleration(speedAcceleration: Double): MovementData = copy(speedAcceleration = speedAcceleration)
	def setGravity(gravity: Vector3): MovementData = copy(gravity = gravity)

	def setConstant(speed: Double): MovementData = copy(speedOriginal = speed, speedLimit = speed, speedAcceleration = 0D)
}

object MovementData {

	final val NbtOriginal     = "original"
	final val NbtLimit        = "limit"
	final val NbtAcceleration = "acceleration"
	final val NbtGravity      = "gravity"

	/**
		* Creates a [[MovementData]] with constant speed and no gravity.
		*/
	def constant(speed: Double): MovementData = MovementData(speed, speed, 0D, Vector3.Zero)

	/**
		* Creates a [[MovementData]] with no gravity.
		*/
	def noGravity(start: Double, limit: Double, acceleration: Double): MovementData = MovementData(start, limit, acceleration, Vector3.Zero)

	def fromNBT(tag: NBTTagCompound): MovementData = {
		val speedOriginal = tag.getDouble(NbtOriginal)
		val speedLimit = tag.getDouble(NbtLimit)
		val speedAcceleration = tag.getDouble(NbtAcceleration)
		val list = tag.getTagList(NbtGravity, Constants.NBT.TAG_DOUBLE)
		val gravity = Vector3(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2))
		MovementData(speedOriginal, speedLimit, speedAcceleration, gravity)
	}
}