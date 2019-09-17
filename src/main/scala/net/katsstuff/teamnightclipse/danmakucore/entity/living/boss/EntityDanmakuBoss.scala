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
package net.katsstuff.teamnightclipse.danmakucore.entity.living.boss

import java.util.UUID
import java.util

import scala.collection.JavaConverters._

import com.google.common.base.Optional

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.Phase
import net.katsstuff.teamnightclipse.danmakucore.entity.living.{EntityDanmakuMob, TouhouCharacter}
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.TouhouHelper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.datasync.{DataSerializers, EntityDataManager}
import net.minecraft.util.DamageSource
import net.minecraft.world.{BossInfo, BossInfoServer, World}
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.teamnightclipse.danmakucore.entity.living.{EntityDanmakuMob, TouhouCharacter}
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.Phase
import net.katsstuff.teamnightclipse.mirror.data.Vector3

object EntityDanmakuBoss {
  private val BossInfoUUID = EntityDataManager.createKey(classOf[EntityDanmakuBoss], DataSerializers.OPTIONAL_UNIQUE_ID)
}
abstract class EntityDanmakuBoss(world: World) extends EntityDanmakuMob(world) {
  setupPhases()
  DanmakuCore.proxy.addDanmakuBoss(this)

  private val bossInfo = new BossInfoServer(this.getDisplayName, BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS)

  override protected def entityInit(): Unit = {
    super.entityInit()
    dataManager.register(EntityDanmakuBoss.BossInfoUUID, Optional.absent[UUID])
  }

  def getBossInfoUUID: UUID =
    dataManager.get(EntityDanmakuBoss.BossInfoUUID).toJavaUtil.toOption.getOrElse(new UUID(0L, 0L))

  override protected def updateAITasks(): Unit = {
    super.updateAITasks()
    this.bossInfo.setColor(
      if (phaseManager.currentPhase.isSpellcard) BossInfo.Color.RED
      else BossInfo.Color.WHITE
    )
    this.bossInfo.setPercent(this.getHealth / this.getMaxHealth)
  }

  override def onLivingUpdate(): Unit = {
    super.onLivingUpdate()
    if (!world.isRemote) dataManager.set(EntityDanmakuBoss.BossInfoUUID, Optional.of(bossInfo.getUniqueId))
  }

  override def onDeath(cause: DamageSource): Unit = if (!phaseManager.hasNextPhase) super.onDeath(cause)

  override protected def onDeathUpdate(): Unit = {
    val oldCurrentPhase = phaseManager.currentPhase
    if (phaseManager.hasNextPhase) {
      phaseManager.nextPhase()
      if (!world.isRemote) {
        setHealth(getMaxHealth)
        val source = getLastDamageSource
        if (source != null) {
          dropPhaseLoot(source)
          oldCurrentPhase.dropLoot(source)
        }
      }
    } else {
      super.onDeathUpdate()
      if (oldCurrentPhase.isActive) oldCurrentPhase.deconstruct()
      DanmakuCore.proxy.removeDanmakuBoss(this)
    }
  }

  def getInvincibleTime: Int = {
    val counter = phaseManager.currentPhase.counter
    if (counter < 0) -counter
    else 0
  }

  override def isEntityInvulnerable(source: DamageSource): Boolean =
    getInvincibleTime > 0 || super.isEntityInvulnerable(source)

  override def addTrackingPlayer(player: EntityPlayerMP): Unit = {
    super.addTrackingPlayer(player)
    bossInfo.addPlayer(player)
  }

  override def removeTrackingPlayer(player: EntityPlayerMP): Unit = {
    super.removeTrackingPlayer(player)
    bossInfo.removePlayer(player)
  }

  override def isNonBoss = false

  private def setupPhases(): Unit = {
    phaseManager.addPhases(phaseList)
    phaseManager.currentPhase.init()
  }

  def remainingSpellcards: Int =
    phaseManager.phaseList.drop(phaseManager.currentPhaseIndex + 1).count(_.isSpellcard)

  def phaseList: Seq[Phase] //TODO Java-API

  def character: TouhouCharacter

  override def syncPhaseManagerToClient = true

  override def powerSpawns: Int = rand.nextInt(8) + 3

  override def pointSpawns: Int = rand.nextInt(10) + 4

  override protected def dropPhaseLoot(source: DamageSource): Unit = {
    super.dropPhaseLoot(source)
    val direction =
      if (source.getImmediateSource != null) Vector3.directionToEntity(this, source.getImmediateSource)
      else Vector3.Down

    if (rand.nextInt(100) < 20) world.spawnEntity(TouhouHelper.createBomb(world, pos, direction))
    if (rand.nextInt(100) < 5) world.spawnEntity(TouhouHelper.createLife(world, pos, direction))
  }
}

abstract class AbstractEntityDanmakuBoss(world: World) extends EntityDanmakuBoss(world) {

  override def phaseList: Seq[Phase] = getPhaseList.asScala

  def getPhaseList: util.List[Phase]
}