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
package net.katsstuff.teamnightclipse.danmakucore.entity.spellcard

import java.util.Random

import net.katsstuff.teamnightclipse.danmakucore.EnumDanmakuLevel
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler
import net.katsstuff.teamnightclipse.danmakucore.misc.LogicalSideOnly
import net.katsstuff.teamnightclipse.mirror.data.Vector3
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
