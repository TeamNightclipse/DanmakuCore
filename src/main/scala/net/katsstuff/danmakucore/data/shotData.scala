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

import io.netty.buffer.ByteBuf
import net.katsstuff.danmakucore.entity.danmaku.form.Form
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType
import net.katsstuff.danmakucore.helper.LogHelper
import net.katsstuff.danmakucore.lib.LibColor
import net.katsstuff.danmakucore.lib.data.{LibForms, LibSubEntities}
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.INBTSerializable

/**
	* Holds general information about the effect
	* and behavior of a [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
	*/
abstract sealed class AbstractShotData {

	/**
		* The physical appearance of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		*/
	def form: Form

	/**
		* The color of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		*/
	def color: Int

	/**
		* The damage the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		* will cause on hit.
		*/
	def damage: Float

	/**
		* The size on the x axis of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		*/
	def sizeX: Float

	/**
		* The size on the y axis of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		*/
	def sizeY: Float

	/**
		* The size on the z axis of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		*/
	def sizeZ: Float

	/**
		* How long the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		* will stand still before activating.
		*/
	def delay: Int

	/**
		* How long the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]]
		* will last before it's killed.
		*/
	def end: Int

	/**
		* The [[SubEntityType]] of the [[net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku]].
		* This is kind of like the AI of the entity, and where all the hard work happens.
		*/
	def subEntity: SubEntityType

	def serializeByteBuf(buf: ByteBuf) {
		buf.writeInt(DanmakuRegistry.FORM.getId(form))
		buf.writeInt(color)
		buf.writeFloat(sizeX)
		buf.writeFloat(sizeY)
		buf.writeFloat(sizeZ)
		buf.writeInt(delay)
		buf.writeInt(end)
		buf.writeInt(DanmakuRegistry.SUB_ENTITY.getId(subEntity))
	}

	def serializeNBT: NBTTagCompound = {
		val tag = new NBTTagCompound
		tag.setString(ShotData.NbtForm, form.getFullName.toString)
		tag.setInteger(ShotData.NbtColor, color)
		tag.setFloat(ShotData.NbtDamage, damage)
		tag.setFloat(ShotData.NbtSizeX, sizeX)
		tag.setFloat(ShotData.NbtSizeY, sizeY)
		tag.setFloat(ShotData.NbtSizeZ, sizeZ)
		tag.setInteger(ShotData.NbtDelay, delay)
		tag.setInteger(ShotData.NbtEnd, end)
		tag.setString(ShotData.NbtSubEntity, subEntity.getFullName.toString)
		tag
	}

	def asMutable: MutableShotData

	def asImmutable: ShotData
}

final case class MutableShotData(
		@BeanProperty var form: Form = LibForms.SPHERE,
		@BeanProperty var color: Int = LibColor.COLOR_SATURATED_RED,
		@BeanProperty var damage: Float = 0.5F,
		@BeanProperty var sizeX: Float = 0.5F,
		@BeanProperty var sizeY: Float = 0.5F,
		@BeanProperty var sizeZ: Float = 0.5F,
		@BeanProperty var delay: Int = 0,
		@BeanProperty var end: Int = 80,
		@BeanProperty var subEntity: SubEntityType = LibSubEntities.DEFAULT_TYPE) extends AbstractShotData with INBTSerializable[NBTTagCompound] {

	//For better java interaction
	def this(form: Form, color: Int) {
		this(form, color, 0.5F, 0.5F, 0.5F, 0.5F)
	}

	def this(form: Form, color: Int, damage: Float) {
		this(form, color, damage, 0.5F, 0.5F, 0.5F)
	}

	def this(form: Form, color: Int, damage: Float, size: Float) {
		this(form, color, damage, size, size, size)
	}

	def this(form: Form, color: Int, damage: Float, size: Float, delay: Int) {
		this(form, color, damage, size, size, size, delay)
	}

	def this(form: Form, color: Int, damage: Float, size: Float, delay: Int, end: Int) {
		this(form, color, damage, size, size, size, delay, end)
	}

	def this(form: Form, color: Int, damage: Float, size: Float, delay: Int, end: Int, subEntity: SubEntityType) {
		this(form, color, damage, size, size, size, delay, end, subEntity)
	}

	def setSize(sizeX: Float, sizeY: Float, sizeZ: Float): MutableShotData = {
		this.sizeX = sizeX
		this.sizeY = sizeY
		this.sizeZ = sizeZ
		this
	}

	def setSize(size: Float): MutableShotData = setSize(size, size, size)

	def scaleSize(scale: Float): MutableShotData = scaleSize(scale, scale, scale)
	def scaleSize(scaleX: Float, scaleY: Float, scaleZ: Float): MutableShotData =
		copy(sizeX = sizeX * scaleX, sizeY = sizeY * scaleY, sizeZ = sizeZ * scaleZ)

	def deserializeByteBuf(buf: ByteBuf) {
		form = Option(DanmakuRegistry.FORM.getObjectById(buf.readInt)).getOrElse {
			LogHelper.warn("Found null form. Setting to default")
			LibForms.SPHERE
		}
		color = buf.readInt
		sizeX = buf.readFloat
		sizeY = buf.readFloat
		sizeZ = buf.readFloat
		delay = buf.readInt
		end = buf.readInt
		subEntity = Option(DanmakuRegistry.SUB_ENTITY.getObjectById(buf.readInt)).getOrElse {
			LogHelper.warn("Found null subEntity type. Setting to default")
			LibSubEntities.DEFAULT_TYPE
		}
	}

	override def deserializeNBT(tag: NBTTagCompound): Unit = {
		form = Option(DanmakuRegistry.FORM.getObject(new ResourceLocation(tag.getString(ShotData.NbtForm)))).getOrElse {
			LogHelper.warn("Found null form. Setting to default")
			LibForms.SPHERE
		}
		color = tag.getInteger(ShotData.NbtColor)
		damage = tag.getFloat(ShotData.NbtDamage)
		sizeX = tag.getFloat(ShotData.NbtSizeX)
		sizeY = tag.getFloat(ShotData.NbtSizeY)
		sizeZ = tag.getFloat(ShotData.NbtSizeZ)
		delay = tag.getInteger(ShotData.NbtDelay)
		end = tag.getInteger(ShotData.NbtEnd)
		subEntity = Option(DanmakuRegistry.SUB_ENTITY.getObject(new ResourceLocation(tag.getString(ShotData.NbtSubEntity)))).getOrElse {
			LogHelper.warn("Found null subEntity type. Setting to default")
			LibSubEntities.DEFAULT_TYPE
		}
	}

	def copyObj: MutableShotData = copy()

	override def asMutable: MutableShotData = this

	override def asImmutable: ShotData = ShotData(form, color, damage, sizeX, sizeY, sizeZ, delay, end, subEntity)
}

final case class ShotData(
		@BeanProperty form: Form = LibForms.SPHERE,
		@BeanProperty color: Int = LibColor.COLOR_SATURATED_RED,
		@BeanProperty damage: Float = 0.5F,
		@BeanProperty sizeX: Float = 0.5F,
		@BeanProperty sizeY: Float = 0.5F,
		@BeanProperty sizeZ: Float = 0.5F,
		@BeanProperty delay: Int = 0,
		@BeanProperty end: Int = 80,
		@BeanProperty subEntity: SubEntityType = LibSubEntities.DEFAULT_TYPE) extends AbstractShotData {

	def this(buf: ByteBuf) {
		this(
			form = Option(DanmakuRegistry.FORM.getObjectById(buf.readInt)).getOrElse {
				LogHelper.warn("Found null form. Setting to default")
				LibForms.SPHERE
			},
			color = buf.readInt,
			sizeX = buf.readFloat,
			sizeY = buf.readFloat,
			sizeZ = buf.readFloat,
			delay = buf.readInt,
			end = buf.readInt,
			subEntity = Option(DanmakuRegistry.SUB_ENTITY.getObjectById(buf.readInt)).getOrElse {
				LogHelper.warn("Found null subEntity type. Setting to default")
				LibSubEntities.DEFAULT_TYPE
			}
		)
	}

	def this(tag: NBTTagCompound) {
		this(
			form = Option(DanmakuRegistry.FORM.getObject(new ResourceLocation(tag.getString(ShotData.NbtForm)))).getOrElse {
				LogHelper.warn("Found null form. Setting to default")
				LibForms.SPHERE
			},
			color = tag.getInteger(ShotData.NbtColor),
			damage = tag.getFloat(ShotData.NbtDamage),
			sizeX = tag.getFloat(ShotData.NbtSizeX),
			sizeY = tag.getFloat(ShotData.NbtSizeY),
			sizeZ = tag.getFloat(ShotData.NbtSizeZ),
			delay = tag.getInteger(ShotData.NbtDelay),
			end = tag.getInteger(ShotData.NbtEnd),
			subEntity = Option(DanmakuRegistry.SUB_ENTITY.getObject(new ResourceLocation(tag.getString(ShotData.NbtSubEntity)))).getOrElse {
				LogHelper.warn("Found null subEntity type. Setting to default")
				LibSubEntities.DEFAULT_TYPE
			}
		)
	}

	//For better java interaction
	def this(form: Form, color: Int) {
		this(form, color, 0.5F, 0.5F, 0.5F, 0.5F)
	}

	def this(form: Form, color: Int, damage: Float) {
		this(form, color, damage, 0.5F, 0.5F, 0.5F)
	}

	def this(form: Form, color: Int, damage: Float, size: Float) {
		this(form, color, damage, size, size, size)
	}

	def this(form: Form, color: Int, damage: Float, size: Float, delay: Int) {
		this(form, color, damage, size, size, size, delay)
	}

	def this(form: Form, color: Int, damage: Float, size: Float, delay: Int, end: Int) {
		this(form, color, damage, size, size, size, delay, end)
	}

	def this(form: Form, color: Int, damage: Float, size: Float, delay: Int, end: Int, subEntity: SubEntityType) {
		this(form, color, damage, size, size, size, delay, end, subEntity)
	}

	def setForm(form: Form): ShotData = copy(form = form)
	def setColor(color: Int): ShotData = copy(color = color)
	def setDamage(damage: Float): ShotData = copy(damage = damage)
	def setSize(sizeX: Float, sizeY: Float, sizeZ: Float): ShotData = copy(sizeX = sizeX, sizeY = sizeY, sizeZ = sizeZ)
	def setSize(size: Float): ShotData = setSize(size, size, size)
	def setSizeX(sizeX: Float): ShotData = copy(sizeX = sizeX)
	def setSizeY(sizeY: Float): ShotData = copy(sizeY = sizeY)
	def setSizeZ(sizeZ: Float): ShotData = copy(sizeZ = sizeZ)
	def setDelay(delay: Int): ShotData = copy(delay = delay)
	def setEnd(end: Int): ShotData = copy(end = end)
	def setSubEntity(subEntity: SubEntityType): ShotData = copy(subEntity = subEntity)

	def scaleSize(scale: Float): ShotData = scaleSize(scale, scale, scale)
	def scaleSize(scaleX: Float, scaleY: Float, scaleZ: Float): ShotData =
		copy(sizeX = sizeX * scaleX, sizeY = sizeY * scaleY, sizeZ = sizeZ * scaleZ)

	override def asMutable: MutableShotData = MutableShotData(form, color, damage, sizeX, sizeY, sizeZ, delay, end, subEntity)

	override def asImmutable: ShotData = this
}

object ShotData {

	final val NbtForm      = "form"
	final val NbtColor     = "color"
	final val NbtDamage    = "damage"
	final val NbtSizeX     = "sizeX"
	final val NbtSizeY     = "sizeY"
	final val NbtSizeZ     = "sizeZ"
	final val NbtDelay     = "delay"
	final val NbtEnd       = "end"
	final val NbtSubEntity = "subEntity"
	final val NbtShotData  = "shotData"

	final val DefaultShotData = ShotData()

	def emptyMutable: MutableShotData = MutableShotData()

	def fromNBTItemStack(stack: ItemStack): ShotData = {
		new ShotData(stack.getSubCompound(NbtShotData, true))
	}

	def serializeNBTItemStack(stack: ItemStack, shotData: AbstractShotData): ItemStack = {
		val rootTag = Option(stack.getTagCompound).getOrElse(new NBTTagCompound)
		rootTag.setTag(NbtShotData, shotData.serializeNBT)
		stack.setTagCompound(rootTag)
		stack
	}
}