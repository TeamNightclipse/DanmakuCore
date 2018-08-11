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
package net.katsstuff.teamnightclipse.danmakucore.javastuff

import java.util.Optional
import java.util.function.Consumer

import javax.annotation.Nullable

import net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata.IDanmakuCoreData
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.katsstuff.teamnightclipse.danmakucore.entity.EntityFallingData
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.{EntitySpellcard, Spellcard}
import net.katsstuff.teamnightclipse.danmakucore.helper.TTouhouHelper
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.{TouhouHelper => ScalaTouhouHelper}
import net.katsstuff.teamnightclipse.danmakucore.misc.LogicalSideOnly
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.Side

object TouhouHelper extends TTouhouHelper {

  /**
    * Checks if a player can declare a given spellcard.
    *
    * @return The target for the spellcard, if a spellcard can be declared.
    * Note that this can return some and the declaration can still fail, if
    * it does, it's because the spellcard denied it.
    */
  def canPlayerDeclareSpellcard(player: EntityPlayer, spellcard: Spellcard): Optional[EntityLivingBase] =
    ScalaTouhouHelper.getSpellcardTarget(player, spellcard).toOptional

  /**
    * Declares a spellcard as a player, doing the necessary checks and
    * changes according to the player.
    *
    * @return The Spellcard if it was spawned.
    */
  def declareSpellcardPlayer(
      player: EntityPlayer,
      spellcard: Spellcard,
      firstAttack: Boolean
  ): Optional[EntitySpellcard] = ScalaTouhouHelper.declareSpellcardPlayer(player, spellcard, firstAttack).toOptional

  /**
    * Declares a spellcard. If you are declaring a spellcard for a player,
    * user [[TouhouHelper.declareSpellcardPlayer]].
    *
    * @return The Spellcard if it was spawned.
    */
  def declareSpellcard(
      user: EntityLivingBase,
      @Nullable target: EntityLivingBase,
      spellCard: Spellcard,
      firstAttack: Boolean,
      addSpellcardName: Boolean
  ): Optional[EntitySpellcard] =
    ScalaTouhouHelper.declareSpellcard(user, Option(target), spellCard, firstAttack, addSpellcardName).toOptional

  /**
    * Tries to get the [[IDanmakuCoreData]] if the provider
    * has the data.
    */
  def getDanmakuCoreData(provider: ICapabilityProvider): Optional[IDanmakuCoreData] =
    ScalaTouhouHelper.getDanmakuCoreData(provider).toOptional

  /**
    * Changes the [[IDanmakuCoreData]] for an entity if it has the data,
    * and then syncs it to players withing range.
    *
    * @param consumer Consumer that changes the data
    * @param target The target entity
    * @param radius The radius to sync in
    */
  @LogicalSideOnly(Side.SERVER)
  def changeAndSyncEntityData(consumer: Consumer[IDanmakuCoreData], target: Entity, radius: Double): Unit =
    ScalaTouhouHelper.changeAndSyncEntityData(consumer.asScala, target, radius)

  /**
    * Changes the [[IDanmakuCoreData]] for a player if the player has
    * the data,and then syncs it to to the player.
    *
    * @param consumer Consumer that changes the data
    * @param player The player to change the data for
    */
  @LogicalSideOnly(Side.SERVER)
  def changeAndSyncPlayerData(consumer: Consumer[IDanmakuCoreData], player: EntityPlayer): Unit =
    ScalaTouhouHelper.changeAndSyncPlayerData(consumer.asScala, player)

  /**
    * Creates a green score entity.
    *
    * @param world The world
    * @param target The target if the entity should home in on the target
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createScoreGreen(world: World, @Nullable target: Entity, pos: Vector3, direction: Vector3): EntityFallingData =
    ScalaTouhouHelper.createScoreGreen(world, Option(target), pos, direction)

  /**
    * Creates a blue score entity.
    *
    * @param world The world
    * @param target The target if the entity should home in on the target
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createScoreBlue(world: World, @Nullable target: Entity, pos: Vector3, direction: Vector3): EntityFallingData =
    ScalaTouhouHelper.createScoreBlue(world, Option(target), pos, direction)

}
