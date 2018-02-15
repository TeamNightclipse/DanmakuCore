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
import scala.collection.JavaConverters._

import io.netty.buffer.ByteBuf
import net.katsstuff.danmakucore.danmaku.form.Form
import net.katsstuff.danmakucore.danmaku.subentity.SubEntityType
import net.katsstuff.danmakucore.helper.LogHelper
import net.katsstuff.danmakucore.lib.LibColor
import net.katsstuff.danmakucore.lib.data.{LibForms, LibSubEntities}
import net.katsstuff.danmakucore.network.scalachannel.MessageConverter
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.INBTSerializable

/**
	* Holds general information about the effect
	* and behavior of a [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
	*/
abstract sealed class AbstractShotData {

  /**
		* The physical appearance of the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		*/
  def form: Form

  /**
    * Extra properties to send the renderer about the danmaku.
    */
  def renderProperties: Map[String, Float]

  /**
		* The edge or colorful color of the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		*/
  def edgeColor: Int

  /**
    * The core or colorless color of the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
    */
  def coreColor: Int

  /**
		* The damage the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		* will cause on hit.
		*/
  def damage: Float

  /**
		* The size on the x axis of the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		*/
  def sizeX: Float

  /**
		* The size on the y axis of the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		*/
  def sizeY: Float

  /**
		* The size on the z axis of the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		*/
  def sizeZ: Float

  /**
		* How long the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		* will stand still before activating.
		*/
  def delay: Int

  /**
		* How long the [[net.katsstuff.danmakucore.danmaku.DanmakuState]]
		* will last before it's killed.
		*/
  def end: Int

  /**
		* The [[SubEntityType]] of the [[net.katsstuff.danmakucore.danmaku.DanmakuState]].
		* This is kind of like the AI of the entity, and where all the hard work happens.
		*/
  def subEntity: SubEntityType

  def serializeByteBuf(buf: ByteBuf) {
    buf.writeInt(DanmakuRegistry.getId(classOf[Form], form))

    {
      val packetBuf = new PacketBuffer(buf)
      packetBuf.writeInt(renderProperties.size)
      for ((k, v) <- renderProperties) {
        require(k.length <= 32, "Too long render property")
        packetBuf.writeString(k)
        packetBuf.writeFloat(v)
      }
    }

    buf.writeInt(edgeColor)
    buf.writeInt(coreColor)
    buf.writeFloat(sizeX)
    buf.writeFloat(sizeY)
    buf.writeFloat(sizeZ)
    buf.writeInt(delay)
    buf.writeInt(end)
    buf.writeInt(DanmakuRegistry.getId(classOf[SubEntityType], subEntity))
  }

  def serializeNBT: NBTTagCompound = {
    val tag = new NBTTagCompound
    tag.setString(ShotData.NbtForm, form.fullNameString)
    tag.setTag(ShotData.NbtRenderProperties, {
      val compound = new NBTTagCompound
      renderProperties.foreach { case (k, v) => compound.setFloat(k, v) }
      compound
    })
    tag.setInteger(ShotData.NbtColor, edgeColor)
    tag.setInteger(ShotData.NbtCoreColor, coreColor)
    tag.setFloat(ShotData.NbtDamage, damage)
    tag.setFloat(ShotData.NbtSizeX, sizeX)
    tag.setFloat(ShotData.NbtSizeY, sizeY)
    tag.setFloat(ShotData.NbtSizeZ, sizeZ)
    tag.setInteger(ShotData.NbtDelay, delay)
    tag.setInteger(ShotData.NbtEnd, end)
    tag.setString(ShotData.NbtSubEntity, subEntity.fullNameString)
    tag
  }

  def asMutable: MutableShotData

  def asImmutable: ShotData
}

final case class MutableShotData(
    @BeanProperty var form: Form = LibForms.SPHERE,
    @BeanProperty var renderProperties: Map[String, Float] = Map.empty,
    @BeanProperty var edgeColor: Int = LibColor.COLOR_SATURATED_RED,
    @BeanProperty var coreColor: Int = 0xFFFFFF,
    @BeanProperty var damage: Float = 0.5F,
    @BeanProperty var sizeX: Float = 0.5F,
    @BeanProperty var sizeY: Float = 0.5F,
    @BeanProperty var sizeZ: Float = 0.5F,
    @BeanProperty var delay: Int = 0,
    @BeanProperty var end: Int = 80,
    @BeanProperty var subEntity: SubEntityType = LibSubEntities.DEFAULT_TYPE
) extends AbstractShotData
    with INBTSerializable[NBTTagCompound] {

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
    form = Option(DanmakuRegistry.getObjById(classOf[Form], buf.readInt())).getOrElse {
      LogHelper.warn("Found null form. Setting to default")
      LibForms.SPHERE
    }
    renderProperties = {
      val packetBuf = new PacketBuffer(buf)
      val amount    = packetBuf.readInt()
      Seq
        .fill(amount) {
          val key   = packetBuf.readString(64)
          val value = packetBuf.readFloat()
          key -> value
        }
        .toMap
    }
    edgeColor = buf.readInt
    coreColor = buf.readInt
    sizeX = buf.readFloat
    sizeY = buf.readFloat
    sizeZ = buf.readFloat
    delay = buf.readInt
    end = buf.readInt
    subEntity = Option(DanmakuRegistry.getObjById(classOf[SubEntityType], buf.readInt())).getOrElse {
      LogHelper.warn("Found null subEntity type. Setting to default")
      LibSubEntities.DEFAULT_TYPE
    }
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    form = Option(DanmakuRegistry.Form.getValue(new ResourceLocation(tag.getString(ShotData.NbtForm)))).getOrElse {
      LogHelper.warn("Found null form. Setting to default")
      LibForms.SPHERE
    }
    renderProperties = {
      val compound = tag.getCompoundTag(ShotData.NbtRenderProperties)
      val keys     = compound.getKeySet.asScala
      keys.map(s => s -> compound.getFloat(s)).toMap
    }
    edgeColor = tag.getInteger(ShotData.NbtColor)
    coreColor = tag.getInteger(ShotData.NbtCoreColor)
    damage = tag.getFloat(ShotData.NbtDamage)
    sizeX = tag.getFloat(ShotData.NbtSizeX)
    sizeY = tag.getFloat(ShotData.NbtSizeY)
    sizeZ = tag.getFloat(ShotData.NbtSizeZ)
    delay = tag.getInteger(ShotData.NbtDelay)
    end = tag.getInteger(ShotData.NbtEnd)
    subEntity = Option(DanmakuRegistry.SubEntity.getValue(new ResourceLocation(tag.getString(ShotData.NbtSubEntity))))
      .getOrElse {
        LogHelper.warn("Found null subEntity type. Setting to default")
        LibSubEntities.DEFAULT_TYPE
      }
  }

  def copyObj: MutableShotData = copy()

  override def asMutable: MutableShotData = this

  override def asImmutable: ShotData =
    ShotData(form, renderProperties, edgeColor, coreColor, damage, sizeX, sizeY, sizeZ, delay, end, subEntity)
}

final case class ShotData(
    @BeanProperty form: Form = LibForms.SPHERE,
    @BeanProperty renderProperties: Map[String, Float] = Map.empty,
    @BeanProperty edgeColor: Int = LibColor.COLOR_SATURATED_RED,
    @BeanProperty coreColor: Int = 0xFFFFFF,
    @BeanProperty damage: Float = 0.5F,
    @BeanProperty sizeX: Float = 0.5F,
    @BeanProperty sizeY: Float = 0.5F,
    @BeanProperty sizeZ: Float = 0.5F,
    @BeanProperty delay: Int = 0,
    @BeanProperty end: Int = 80,
    @BeanProperty subEntity: SubEntityType = LibSubEntities.DEFAULT_TYPE
) extends AbstractShotData {

  def this(buf: ByteBuf) {
    this(
      form = Option(DanmakuRegistry.getObjById(classOf[Form], buf.readInt)).getOrElse {
        LogHelper.warn("Found null form. Setting to default")
        LibForms.SPHERE
      },
      renderProperties = {
        val packetBuf = new PacketBuffer(buf)
        val amount    = packetBuf.readInt()
        Seq
          .fill(amount) {
            val key   = packetBuf.readString(64)
            val value = packetBuf.readFloat()
            key -> value
          }
          .toMap
      },
      edgeColor = buf.readInt,
      coreColor = buf.readInt,
      sizeX = buf.readFloat,
      sizeY = buf.readFloat,
      sizeZ = buf.readFloat,
      delay = buf.readInt,
      end = buf.readInt,
      subEntity = Option(DanmakuRegistry.getObjById(classOf[SubEntityType], buf.readInt)).getOrElse {
        LogHelper.warn("Found null subEntity type. Setting to default")
        LibSubEntities.DEFAULT_TYPE
      }
    )
  }

  def this(tag: NBTTagCompound) {
    this(
      form = Option(DanmakuRegistry.Form.getValue(new ResourceLocation(tag.getString(ShotData.NbtForm)))).getOrElse {
        LogHelper.warn("Found null form. Setting to default")
        LibForms.SPHERE
      },
      renderProperties = {
        val compound = tag.getCompoundTag(ShotData.NbtRenderProperties)
        val keys     = compound.getKeySet.asScala
        keys.map(s => s -> compound.getFloat(s)).toMap
      },
      edgeColor = tag.getInteger(ShotData.NbtColor),
      coreColor = tag.getInteger(ShotData.NbtCoreColor),
      damage = tag.getFloat(ShotData.NbtDamage),
      sizeX = tag.getFloat(ShotData.NbtSizeX),
      sizeY = tag.getFloat(ShotData.NbtSizeY),
      sizeZ = tag.getFloat(ShotData.NbtSizeZ),
      delay = tag.getInteger(ShotData.NbtDelay),
      end = tag.getInteger(ShotData.NbtEnd),
      subEntity = Option(DanmakuRegistry.SubEntity.getValue(new ResourceLocation(tag.getString(ShotData.NbtSubEntity))))
        .getOrElse {
          LogHelper.warn("Found null subEntity type. Setting to default")
          LibSubEntities.DEFAULT_TYPE
        }
    )
  }

  def setForm(form: Form):                                 ShotData = copy(form = form)
  def setRenderProperties(properties: Map[String, Float]): ShotData = copy(renderProperties = properties)
  def setEdgeColor(color: Int):                            ShotData = copy(edgeColor = color)
  def setCoreColor(color: Int):                            ShotData = copy(coreColor = color)
  def setDamage(damage: Float):                            ShotData = copy(damage = damage)
  def setSize(sizeX: Float, sizeY: Float, sizeZ: Float):   ShotData = copy(sizeX = sizeX, sizeY = sizeY, sizeZ = sizeZ)
  def setSize(size: Float):                                ShotData = setSize(size, size, size)
  def setSizeX(sizeX: Float):                              ShotData = copy(sizeX = sizeX)
  def setSizeY(sizeY: Float):                              ShotData = copy(sizeY = sizeY)
  def setSizeZ(sizeZ: Float):                              ShotData = copy(sizeZ = sizeZ)
  def setDelay(delay: Int):                                ShotData = copy(delay = delay)
  def setEnd(end: Int):                                    ShotData = copy(end = end)
  def setSubEntity(subEntity: SubEntityType):              ShotData = copy(subEntity = subEntity)

  def scaleSize(scale: Float): ShotData = scaleSize(scale, scale, scale)
  def scaleSize(scaleX: Float, scaleY: Float, scaleZ: Float): ShotData =
    copy(sizeX = sizeX * scaleX, sizeY = sizeY * scaleY, sizeZ = sizeZ * scaleZ)

  override def asMutable: MutableShotData =
    MutableShotData(form, renderProperties, edgeColor, coreColor, damage, sizeX, sizeY, sizeZ, delay, end, subEntity)

  override def asImmutable: ShotData = this
}

object ShotData {

  final val NbtForm             = "form"
  final val NbtRenderProperties = "renderProperties"
  final val NbtColor            = "color"
  final val NbtCoreColor        = "coreColor"
  final val NbtDamage           = "damage"
  final val NbtSizeX            = "sizeX"
  final val NbtSizeY            = "sizeY"
  final val NbtSizeZ            = "sizeZ"
  final val NbtDelay            = "delay"
  final val NbtEnd              = "end"
  final val NbtSubEntity        = "subEntity"
  final val NbtShotData         = "shotData"

  final val DefaultShotData = ShotData()

  def emptyMutable: MutableShotData = MutableShotData()

  def fromNBTItemStack(stack: ItemStack): ShotData =
    new ShotData(Option(stack.getSubCompound(NbtShotData)).getOrElse(new NBTTagCompound))

  def serializeNBTItemStack(stack: ItemStack, shotData: AbstractShotData): ItemStack = {
    val rootTag = Option(stack.getTagCompound).getOrElse(new NBTTagCompound)
    rootTag.setTag(NbtShotData, shotData.serializeNBT)
    stack.setTagCompound(rootTag)
    stack
  }

  implicit val converter: MessageConverter[ShotData] =
    MessageConverter[NBTTagCompound].modify(new ShotData(_))(_.serializeNBT)
}
