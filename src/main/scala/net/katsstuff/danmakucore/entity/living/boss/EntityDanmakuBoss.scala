/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.boss

import java.util
import java.util.UUID

import com.google.common.base.Optional

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob
import net.katsstuff.danmakucore.entity.living.TouhouCharacter
import net.katsstuff.danmakucore.entity.living.phase.Phase
import net.katsstuff.danmakucore.helper.TTouhouHelper
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.DamageSource
import net.minecraft.world.BossInfo
import net.minecraft.world.BossInfoServer
import net.minecraft.world.World

object EntityDanmakuBoss {
  private val BossInfoUUID = EntityDataManager.createKey(classOf[EntityDanmakuBoss], DataSerializers.OPTIONAL_UNIQUE_ID)
}
abstract class EntityDanmakuBoss(val world: World) extends EntityDanmakuMob(world) {
  setupPhases()
  DanmakuCore.proxy.addDanmakuBoss(this)

  final private val bossInfo = new BossInfoServer(this.getDisplayName, BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS)

  override protected def entityInit(): Unit = {
    super.entityInit()
    dataManager.register(EntityDanmakuBoss.BossInfoUUID, Optional.absent)
  }

  def getBossInfoUUID: UUID = dataManager.get(EntityDanmakuBoss.BossInfoUUID).or(new UUID(0L, 0L))

  override protected def updateAITasks(): Unit = {
    super.updateAITasks()
    this.bossInfo.setColor(
      if (phaseManager.getCurrentPhase.isSpellcard) BossInfo.Color.RED
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
    val oldCurrentPhase = phaseManager.getCurrentPhase
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
    val counter = phaseManager.getCurrentPhase.getCounter
    if (counter < 0) -counter
    else 0
  }

  override def isEntityInvulnerable(source: DamageSource): Boolean =
    getInvincibleTime > 0 ||
      super.isEntityInvulnerable(source)

  override def addTrackingPlayer(player: EntityPlayerMP): Unit = {
    super.addTrackingPlayer(player)
    this.bossInfo.addPlayer(player)
  }

  override def removeTrackingPlayer(player: EntityPlayerMP): Unit = {
    super.removeTrackingPlayer(player)
    this.bossInfo.removePlayer(player)
  }

  override def isNonBoss = false

  private def setupPhases(): Unit = {
    phaseManager.addPhases(getPhaseList)
    phaseManager.getCurrentPhase.init()
  }

  def remainingSpellcards: Int =
    phaseManager.getPhaseList.stream.skip(phaseManager.getCurrentPhaseIndex + 1L).filter(_.isSpellcard).count.toInt
  def getPhaseList: util.List[Phase]

  def getCharacter: TouhouCharacter

  override def syncPhaseManagerToClient = true

  override protected def dropPhaseLoot(source: DamageSource): Unit = {
    super.dropPhaseLoot(source)
    val direction =
      if (source.getImmediateSource != null) Vector3.directionToEntity(this, source.getImmediateSource)
      else Vector3.Down
    val powerSpawns = rand.nextInt(8)
    for (_ <- 0 until powerSpawns) {
      world.spawnEntity(TouhouHelper.createPower(world, pos, direction))
    }
    val pointSpawns = rand.nextInt(10)
    for (_ <- 0 until pointSpawns) {
      world.spawnEntity(TouhouHelper.createScoreBlue(world, null, pos, direction))
    }
    if (rand.nextInt(100) < 20) world.spawnEntity(TouhouHelper.createBomb(world, pos, direction))
    if (rand.nextInt(100) < 5) world.spawnEntity(TouhouHelper.createLife(world, pos, direction))
  }
}
