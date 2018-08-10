/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.scalastuff

import net.katsstuff.danmakucore.capability.dancoredata.{CapabilityDanCoreDataJ, IDanmakuCoreData}
import net.katsstuff.danmakucore.entity.spellcard.{EntitySpellcard, Spellcard}
import net.katsstuff.danmakucore.entity.{EntityFallingData, FallingDataTypes}
import net.katsstuff.danmakucore.helper.{RemoveMode, TTouhouHelper}
import net.katsstuff.danmakucore.misc.LogicalSideOnly
import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.TargetPoint
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityAgeable, EntityLivingBase}
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.Side

object TouhouHelper extends TTouhouHelper {

  /**
    * Get the target of a spellcard if a player can declare one.
    *
    * @return The target for the spellcard, if a spellcard can be declared.
    * Note that this can return some and the declaration can still fail, if
    * it does, it's because the spellcard denied it.
    */
  def getSpellcardTarget(player: EntityPlayer, spellcard: Spellcard): Option[EntityLivingBase] = {
    val world = player.world
    //Only allow spellcard if use user isn't already using a spellcard
    val spellcardList =
      world.collectEntitiesWithinAABB[EntitySpellcard, EntitySpellcard](player.getEntityBoundingBox.grow(32D)) {
        case entitySpellcard: EntitySpellcard if player == entitySpellcard.user => entitySpellcard
      }

    if (spellcardList.isEmpty) {
      val optTarget = Vector3.collectEntityLookedAt(player) {
        case targetEntity: EntityLivingBase if !targetEntity.isInstanceOf[EntityAgeable] => targetEntity
      }

      optTarget.flatMap { target =>
        if (!player.capabilities.isCreativeMode) {
          val neededBombs  = spellcard.level
          val currentBombs = getDanmakuCoreData(player).fold(0)(_.getBombs)
          if (currentBombs >= neededBombs) Some(target) else None
        } else Some(target)
      }
    } else None
  }

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
  ): Option[EntitySpellcard] = {
    getSpellcardTarget(player, spellcard).flatMap { target =>
      if (!player.capabilities.isCreativeMode)
        changeAndSyncPlayerData((data: IDanmakuCoreData) => data.addBombs(-spellcard.level), player)
      declareSpellcard(player, Some(target), spellcard, firstAttack, addSpellcardName = true)
    }
  }

  /**
    * Declares a spellcard. If you are declaring a spellcard for a player,
    * user [[TouhouHelper.declareSpellcardPlayer]].
    *
    * @return The Spellcard if it was spawned.
    */
  def declareSpellcard(
      user: EntityLivingBase,
      target: Option[EntityLivingBase],
      spellCard: Spellcard,
      firstAttack: Boolean,
      addSpellcardName: Boolean
  ): Option[EntitySpellcard] = {
    if (spellCard.beforeDeclare(user, target.orNull, firstAttack)) {
      val entitySpellCard = new EntitySpellcard(user.world, user, target, spellCard, addSpellcardName)
      user.world.spawnEntity(entitySpellCard)
      if (firstAttack) DanmakuHelper.removeDanmaku(user, 40.0F, RemoveMode.Enemy, dropBonus = true)
      Some(entitySpellCard)
    } else None
  }

  /**
    * Tries to get the [[IDanmakuCoreData]] if the provider
    * has the data.
    */
  def getDanmakuCoreData(provider: ICapabilityProvider): Option[IDanmakuCoreData] =
    if (provider.hasCapability(CapabilityDanCoreDataJ.CORE_DATA, null))
      Some(provider.getCapability(CapabilityDanCoreDataJ.CORE_DATA, null))
    else None

  /**
    * Changes the [[IDanmakuCoreData]] for an entity if it has the data,
    * and then syncs it to players withing range.
    *
    * @param dataRunnable A function that changes the data
    * @param target The target entity
    * @param radius The radius to sync in
    */
  @LogicalSideOnly(Side.SERVER)
  def changeAndSyncEntityData(dataRunnable: IDanmakuCoreData => Unit, target: Entity, radius: Double): Unit =
    getDanmakuCoreData(target).foreach { data =>
      dataRunnable(data)
      val point = TargetPoint.around(target, radius)
      data.syncToClose(point, target)
    }

  /**
    * Changes the [[IDanmakuCoreData]] for a player if the player has
    * the data,and then syncs it to to the player.
    *
    * @param dataRunnable A function that changes the data
    * @param player The player to change the data for
    */
  @LogicalSideOnly(Side.SERVER)
  def changeAndSyncPlayerData(dataRunnable: IDanmakuCoreData => Unit, player: EntityPlayer): Unit =
    getDanmakuCoreData(player).foreach { data =>
      dataRunnable(data)
      player match {
        case p: EntityPlayerMP => data.syncTo(p, player)
        case _                 =>
      }
    }

  /**
    * Creates a green score entity.
    *
    * @param world The world
    * @param target The target if the entity should home in on the target
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createScoreGreen(world: World, target: Option[Entity], pos: Vector3, direction: Vector3): EntityFallingData =
    new EntityFallingData(
      world,
      FallingDataTypes.scoreGreen,
      fuzzPosition(pos),
      Vector3.limitRandomDirection(direction, 7.5F),
      target,
      10
    )

  /**
    * Creates a blue score entity.
    *
    * @param world The world
    * @param target The target if the entity should home in on the target
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createScoreBlue(world: World, target: Option[Entity], pos: Vector3, direction: Vector3): EntityFallingData =
    new EntityFallingData(
      world,
      FallingDataTypes.scoreBlue,
      fuzzPosition(pos),
      Vector3.limitRandomDirection(direction, 7.5F),
      target,
      100
    )
}
