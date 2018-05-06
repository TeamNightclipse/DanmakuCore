/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard

import java.util.Random

import net.katsstuff.danmakucore.EnumDanmakuLevel
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.katsstuff.danmakucore.misc.LogicalSideOnly
import net.katsstuff.mirror.data.Vector3
import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.relauncher.Side

@LogicalSideOnly(Side.SERVER)
abstract class SpellcardEntity(
    val spellcard: Spellcard,
    val cardEntity: EntitySpellcard,
    var target: Option[EntityLivingBase]
) extends INBTSerializable[NBTTagCompound] {
  private val NbtTime = "time"

  val rng                            = new Random
  protected var time                 = 0
  var danmakuLevel: EnumDanmakuLevel = ConfigHandler.danmaku.danmakuLevel

  def onUpdate(): Unit = {
    time += 1
    onSpellcardUpdate()
  }

  def onSpellcardUpdate(): Unit

  def user: EntityLivingBase = cardEntity.user

  def name: TextComponentTranslation = new TextComponentTranslation(spellcard.unlocalizedName)

  def world: World = cardEntity.world

  def posUser: Vector3 = new Vector3(user)
  def posCard: Vector3 = new Vector3(cardEntity)

  def posTarget: Option[Vector3]             = target.map(new Vector3(_))
  def directionUserToTarget: Option[Vector3] = posTarget.map(posB => Vector3.directionToPos(posUser, posB).asImmutable)

  override def serializeNBT: NBTTagCompound = {
    val tag = new NBTTagCompound
    tag.setInteger(NbtTime, time)
    tag
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = time = tag.getInteger(NbtTime)
}
