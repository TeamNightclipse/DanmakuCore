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

import net.katsstuff.danmakucore.helper.NBTHelper
import net.minecraft.nbt.NBTTagCompound

/**
	* Defines how a [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]] will move.
	*/
abstract sealed class AbstractMovementData {

	/**
		* The speed that the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]] starts with.
		*/
	def speedOriginal: Double

	/**
		* The upper limit of the speed of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]].
		*/
	def upperSpeedLimit: Double

	/**
		* The lower limit of the speed of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]].
		*/
	def lowerSpeedLimit: Double

	/**
		* The change in speed each tick.
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
		tag.setDouble(MovementData.NbtUpperLimit, upperSpeedLimit)
		tag.setDouble(MovementData.NbtLowerLimit, lowerSpeedLimit)
		tag.setDouble(MovementData.NbtAcceleration, speedAcceleration)

		NBTHelper.setVector(tag, MovementData.NbtGravity, gravity)
		tag
	}
}

final case class MutableMovementData(
		@BeanProperty var speedOriginal: Double,
		@BeanProperty var upperSpeedLimit: Double,
		@BeanProperty var lowerSpeedLimit: Double,
		@BeanProperty var speedAcceleration: Double,
		@BeanProperty var gravity: Vector3) extends AbstractMovementData {

	def copyObj: MutableMovementData = copy()
}

final case class MovementData(
		@BeanProperty speedOriginal: Double,
		@BeanProperty upperSpeedLimit: Double,
		@BeanProperty lowerSpeedLimit: Double,
		@BeanProperty speedAcceleration: Double,
		@BeanProperty gravity: Vector3) extends AbstractMovementData {

	def setSpeedOriginal(speedOriginal: Double): MovementData = copy(speedOriginal = speedOriginal)
	def setSpeedLimit(speedLimit: Double): MovementData = copy(upperSpeedLimit = speedLimit)
	def setSpeedAcceleration(speedAcceleration: Double): MovementData = copy(speedAcceleration = speedAcceleration)
	def setGravity(gravity: Vector3): MovementData = copy(gravity = gravity)

	def setConstant(speed: Double): MovementData = copy(speedOriginal = speed, upperSpeedLimit = speed, speedAcceleration = 0D)
}

object MovementData {

	final val NbtOriginal     = "original"
	final val NbtUpperLimit   = "upperLimit"
	final val NbtLowerLimit   = "lowerLimit"
	final val NbtAcceleration = "acceleration"
	final val NbtGravity      = "gravity"

	/**
		* Creates a [[MovementData]] with constant speed and no gravity.
		*/
	def constant(speed: Double): MovementData = MovementData(speed, speed, speed, 0D, Vector3.Zero)

	/**
		* Creates a [[MovementData]] with no gravity.
		*/
	def noGravity(start: Double, upperLimit: Double, lowerLimit: Double, acceleration: Double): MovementData = MovementData(start, upperLimit, lowerLimit, acceleration, Vector3.Zero)

	def fromNBT(tag: NBTTagCompound): MovementData = {
		val speedOriginal = tag.getDouble(NbtOriginal)
		val upperSpeedLimit = tag.getDouble(NbtUpperLimit)
		val lowerSpeedLimit = tag.getDouble(NbtLowerLimit)
		val speedAcceleration = tag.getDouble(NbtAcceleration)
		val gravity = NBTHelper.getVector(tag, MovementData.NbtGravity)
		MovementData(speedOriginal, upperSpeedLimit, lowerSpeedLimit, speedAcceleration, gravity)
	}
}