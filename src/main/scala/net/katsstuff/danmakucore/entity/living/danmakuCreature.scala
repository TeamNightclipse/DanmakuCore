/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living

import net.katsstuff.danmakucore.EnumDanmakuLevel
import net.katsstuff.danmakucore.danmaku.DamageSourceDanmaku
import net.katsstuff.danmakucore.entity.living.ai.{EntityHoverHelper, PathNavigateHover}
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.katsstuff.danmakucore.network.{DanCorePacketHandler, PhaseDataPacket}
import net.katsstuff.danmakucore.scalastuff.{DanmakuHelper, TouhouHelper}
import net.katsstuff.mirror.data.Vector3
import net.katsstuff.mirror.network.scalachannel.TargetPoint
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityFlying
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.{EntityCreature, MoverType, SharedMonsterAttributes}
import net.minecraft.init.SoundEvents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.pathfinding.PathNavigate
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{DamageSource, SoundEvent}
import net.minecraft.world.World

trait TEntityDanmakuCreature extends EntityCreature with EntityFlying {

  protected var species: TouhouSpecies = TouhouSpecies.OTHERS

  override def applyEntityAttributes(): Unit = {
    super.applyEntityAttributes()
    this.getAttributeMap.registerAttribute(SharedMonsterAttributes.FLYING_SPEED)
  }

  override protected def createNavigator(worldIn: World): PathNavigate = {
    val navigateFlying = new PathNavigateHover(this, worldIn)
    navigateFlying.setCanOpenDoors(false)
    navigateFlying.setCanFloat(true)
    navigateFlying.setCanEnterDoors(true)
    navigateFlying
  }

  override def travel(strafe: Float, vertical: Float, forward: Float): Unit = {
    if (isServerWorld && isFlying) {
      moveRelative(strafe, vertical, forward, 0.1F)
      move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)
      motionX *= 0.9D
      motionY *= 0.9D
      motionZ *= 0.9D
    } else super.travel(strafe, vertical, forward)
  }

  override def getBlockPathWeight(pos: BlockPos): Float =
    if (this.world.isAirBlock(pos)) 5F + super.getBlockPathWeight(pos)
    else super.getBlockPathWeight(pos)

  //super.getBlockPathWeight(pos)

  override def isOnLadder: Boolean = !isFlying && super.isOnLadder

  override def fall(distance: Float, damageMultiplier: Float): Unit =
    if (!isFlying) super.fall(distance, damageMultiplier)

  override protected def updateFallState(y: Double, onGroundIn: Boolean, state: IBlockState, pos: BlockPos): Unit =
    if (!isFlying) super.updateFallState(y, onGroundIn, state, pos)

  override def getMaxFallHeight: Int = if (isFlying) 16 else super.getMaxFallHeight

  def isFlying: Boolean = !onGround

  def getSpecies: TouhouSpecies = species

  protected def setSpecies(species: TouhouSpecies): Unit =
    this.species = species

  protected def setMaxHP(hp: Float): Unit =
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(hp)

  def setGroundSpeed(speed: Double): Unit =
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed)

  def getGroundSpeed: Double = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue

  def setFlyingSpeed(speed: Double): Unit =
    getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(speed)

  def getFlyingSpeed: Double = getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getBaseValue

  def pos = new Vector3(this)

  override protected def getSoundVolume = 0.3F

  override protected def getSoundPitch: Float = super.getSoundPitch * 1.95F

  override protected def getAmbientSound: SoundEvent =
    if (getRNG.nextInt(4) != 0) null
    else SoundEvents.ENTITY_BAT_AMBIENT

  override protected def getHurtSound(damageSourceIn: DamageSource): SoundEvent = SoundEvents.ENTITY_BAT_HURT

  override protected def getDeathSound: SoundEvent = SoundEvents.ENTITY_BAT_DEATH
}

class EntityDanmakuCreature(world: World) extends EntityCreature(world) with TEntityDanmakuCreature {
  moveHelper = new EntityHoverHelper(this)
}

class EntityDanmakuMob(world: World) extends EntityMob(world) with TEntityDanmakuCreature {
  moveHelper = new EntityHoverHelper(this)

  private val NbtPhaseManager = "phaseManager"

  protected var phaseManager = new PhaseManager(this)

  override protected def onDeathUpdate(): Unit = {
    super.onDeathUpdate()
    if (getLastDamageSource.isInstanceOf[DamageSourceDanmaku] && deathTime == 7) {
      DanmakuHelper.explosionEffect2(world, pos, 1.0F + deathTime * 0.1F)
      DanmakuHelper.chainExplosion(this, 5.0F, 5.0F)
    }
  }

  override def onUpdate(): Unit = {
    if (ConfigHandler.danmaku.danmakuLevel eq EnumDanmakuLevel.PEACEFUL) setAttackTarget(null)
    super.onUpdate()
    phaseManager.tick()
  }

  override def addTrackingPlayer(player: EntityPlayerMP): Unit =
    phaseManager.currentPhase.addTrackingPlayer(player)

  override def removeTrackingPlayer(player: EntityPlayerMP): Unit =
    phaseManager.currentPhase.removeTrackingPlayer(player)

  override def attackEntityFrom(damageSource: DamageSource, damage: Float): Boolean = {
    val usedDamage = if (!damageSource.isInstanceOf[DamageSourceDanmaku]) damage * 0.4F else damage
    super.attackEntityFrom(damageSource, usedDamage)
  }

  def getPhaseManager: PhaseManager = phaseManager

  def syncPhaseManagerToClient: Boolean = false

  override def readEntityFromNBT(tag: NBTTagCompound): Unit = {
    super.readEntityFromNBT(tag)
    val phaseTag = tag.getCompoundTag(NbtPhaseManager)
    phaseManager.deserializeNBT(phaseTag)
    if (syncPhaseManagerToClient)
      DanCorePacketHandler.sendToAllAround(new PhaseDataPacket(this, phaseTag), TargetPoint.around(this, 64))
  }

  override def writeEntityToNBT(tag: NBTTagCompound): Unit = {
    super.writeEntityToNBT(tag)
    tag.setTag(NbtPhaseManager, phaseManager.serializeNBT)
  }

  /**
    * How many power entities to spawn when this entity dies.
    */
  def powerSpawns: Int = rand.nextInt(4) + 1

  /**
    * How many point entities to spawn when this entity dies.
    */
  def pointSpawns: Int = rand.nextInt(5) + 1

  /**
    * Loot that is dropped every phase.
    */
  protected def dropPhaseLoot(source: DamageSource): Unit = {
    val direction =
      if (source.getImmediateSource != null) Vector3.directionToEntity(this, source.getImmediateSource)
      else Vector3.Down
    for (_ <- 0 until powerSpawns) {
      world.spawnEntity(TouhouHelper.createPower(world, pos, direction))
    }
    for (_ <- 0 until pointSpawns) {
      world.spawnEntity(TouhouHelper.createScoreBlue(world, None, pos, direction))
    }
  }

  override protected def dropLoot(wasRecentlyHit: Boolean, lootingModifier: Int, source: DamageSource): Unit = {
    dropPhaseLoot(source)
    phaseManager.currentPhase.dropLoot(source)
    super.dropLoot(wasRecentlyHit, lootingModifier, source)
  }
}
