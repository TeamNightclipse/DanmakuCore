/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard

import net.katsstuff.danmakucore.entity.EntityInfo
import net.katsstuff.danmakucore.entity.spellcard.spellcardbar.SpellcardInfoServer
import net.katsstuff.danmakucore.helper.{NBTHelper, RemoveMode}
import net.katsstuff.danmakucore.lib.LibEntityName
import net.katsstuff.danmakucore.misc.LogicalSideOnly
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.danmakucore.scalastuff.DanmakuHelper
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.nbt.{NBTTagCompound, NBTUtil}
import net.minecraft.network.datasync.{DataSerializers, EntityDataManager}
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side

object EntitySpellcard {
  implicit val info: EntityInfo[EntitySpellcard] = new EntityInfo[EntitySpellcard] {
    override def create(world: World): EntitySpellcard = new EntitySpellcard(world)

    override def name: String = LibEntityName.SPELLCARD
  }

  private val SpellcardId = EntityDataManager.createKey(classOf[EntitySpellcard], DataSerializers.VARINT)
}
class EntitySpellcard(
    world: World,
    @LogicalSideOnly(Side.SERVER) var user: EntityLivingBase,
    target: Option[EntityLivingBase],
    @LogicalSideOnly(Side.SERVER) spellcardTpe: Spellcard,
    sendNamePacket: Boolean
) extends Entity(world) {
  private val NbtSpellcardType = "spellcardType"
  private val NbtSpellcardData = "spellcardData"
  private val NbtUser          = "user"

  @LogicalSideOnly(Side.SERVER)
  private var _spellcardEntity = if (spellcardTpe != null) {
    spellcardId = DanmakuRegistry.getId(classOf[Spellcard], spellcardTpe)
    spellcardTpe.instantiate(this, target)
  } else null

  preventEntitySpawning = true
  setSize(0.4F, 0.6F)

  if (user != null) {
    setPosition(user.posX, user.posY + user.getEyeHeight, user.posZ)
    setRotation(user.rotationYaw, user.rotationPitch)
  }

  @LogicalSideOnly(Side.SERVER)
  private val spellcardInfo =
    if (sendNamePacket && _spellcardEntity != null) new SpellcardInfoServer(_spellcardEntity.name) else null

  def this(world: World) = this(world, null, None, null, true)

  def this(user: EntityLivingBase, target: Option[EntityLivingBase], spellcard: Spellcard, sendInfoPacket: Boolean) {
    this(user.world, user, target, spellcard, sendInfoPacket)
  }

  override def onUpdate(): Unit = {
    if (!world.isRemote && (_spellcardEntity == null || ticksExisted >= spellcardTpe.endTime || user == null)) {
      setDead()
    } else {
      super.onUpdate()
      if (spellcardInfo != null) spellcardInfo.tick()

      if (!world.isRemote) {
        if(user.isDead) {
          DanmakuHelper.removeDanmaku(user, 40F, RemoveMode.Enemy, dropBonus = true)
          setDead()
        }
        else {
          _spellcardEntity.onUpdate()

          if (user.isInstanceOf[EntityPlayer] && ticksExisted < spellcardTpe.removeTime) {
            DanmakuHelper.removeDanmaku(user, 40.0F, RemoveMode.Other, dropBonus = true)
          }
        }
      }
    }
  }

  override def addTrackingPlayer(player: EntityPlayerMP): Unit = if (sendNamePacket) spellcardInfo.addPlayer(player)

  override def removeTrackingPlayer(player: EntityPlayerMP): Unit =
    if (sendNamePacket) spellcardInfo.removePlayer(player)

  override protected def entityInit(): Unit = dataManager.register(EntitySpellcard.SpellcardId, Int.box(0))

  private def spellcardId_=(number: Int): Unit = dataManager.set(EntitySpellcard.SpellcardId, Int.box(number))

  private def spellcardId: Int = dataManager.get(EntitySpellcard.SpellcardId)

  @LogicalSideOnly(Side.SERVER)
  def updateName(): Unit = if (spellcardInfo != null) spellcardInfo.setName(_spellcardEntity.name)

  override protected def readEntityFromNBT(tag: NBTTagCompound): Unit = {
    val userUuid = NBTUtil.getUUIDFromTag(tag.getCompoundTag(NbtUser))
    user = Option(world.getPlayerEntityByUUID(userUuid))
      .orElse(NBTHelper.getEntityByUUID(userUuid, world).toOption.collect { case living: EntityLivingBase => living })
      .orNull

    if (user != null) {
      val tpe = DanmakuRegistry.Spellcard.getValue(new ResourceLocation(tag.getString(NbtSpellcardType)))
      if (tpe == null) setDead()
      else {
        spellcardId = DanmakuRegistry.getId(classOf[Spellcard], tpe)
        _spellcardEntity = tpe.instantiate(this, user)
        _spellcardEntity.deserializeNBT(tag.getCompoundTag(NbtSpellcardData))
      }
    } else setDead()
  }

  override protected def writeEntityToNBT(tag: NBTTagCompound): Unit = {
    tag.setString(NbtSpellcardType, spellcardTpe.fullNameString)
    tag.setTag(NbtSpellcardData, _spellcardEntity.serializeNBT)
    tag.setUniqueId(NbtUser, user.getUniqueID)
  }

  override def canBePushed = false

  override def canBeCollidedWith = false

  override def getBrightness = 0.5F

  @LogicalSideOnly(Side.SERVER)
  def getUser: EntityLivingBase = user

  @LogicalSideOnly(Side.SERVER)
  def spellCardEntity: SpellcardEntity = _spellcardEntity

  @LogicalSideOnly(Side.SERVER)
  def getSpellCardEntity: SpellcardEntity = spellCardEntity

  def getSpellcardType: Spellcard = DanmakuRegistry.getObjById(classOf[Spellcard], spellcardId)
}
