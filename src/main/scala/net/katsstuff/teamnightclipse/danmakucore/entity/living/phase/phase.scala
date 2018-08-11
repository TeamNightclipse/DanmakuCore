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
package net.katsstuff.teamnightclipse.danmakucore.entity.living.phase

import java.util.Optional

import net.katsstuff.teamnightclipse.danmakucore.EnumDanmakuLevel
import net.katsstuff.teamnightclipse.danmakucore.entity.living.EntityDanmakuMob
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.Spellcard
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.spellcardbar.SpellcardInfoServer
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler
import net.katsstuff.teamnightclipse.danmakucore.registry.RegistryValue
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.teamnightclipse.danmakucore.entity.living.EntityDanmakuMob
import net.katsstuff.teamnightclipse.danmakucore.misc.LogicalSideOnly
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.text.{ITextComponent, TextComponentTranslation}
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.relauncher.Side

object Phase {
  private[danmakucore] val NbtName = "name"
}
abstract class Phase(val manager: PhaseManager) extends INBTSerializable[NBTTagCompound] {
  private val NbtCounter     = "counter"
  private val NbtInterval    = "interval"
  private val NbtOldInterval = "oldInterval"

  /**
    * The counter for this [[Phase]]. By default it is incremented each
    * tick. If this is less than 0, it will signal that the entity using
    * this phase is invulnerable, and provided data that can be used on the
    * client.
    */
  protected var _counter = 0

  /**
    * The set amount of time that this phase will use to reset the counter.
    */
  protected var interval  = 0
  private var oldInterval = -1

  /**
    * The level to use for this [[Phase]]. Use this instead of getting
    * it from the config as it can be changes without changing the global
    * difficulty.
    */
  protected var level: EnumDanmakuLevel = ConfigHandler.danmaku.danmakuLevel

  private var _isActive                                  = false
  private var spellcardInfo: Option[SpellcardInfoServer] = None

  def counter: Int                       = _counter
  def getCounter: Int                    = counter
  protected def setCounter(i: Int): Unit = _counter = i

  protected def getInterval: Int          = interval
  protected def setInterval(i: Int): Unit = interval = i

  protected def getLevel: EnumDanmakuLevel            = level
  protected def setLevel(lvl: EnumDanmakuLevel): Unit = level = lvl

  /**
    * Initiate the state of this [[Phase]]
    */
  def init(): Unit = {
    _isActive = true
    _counter = 0
    interval = 40

    spellcardName.foreach { name =>
      if (!entity.world.isRemote) {
        spellcardInfo = Some(new SpellcardInfoServer(name))
        entity.world
          .entitiesWithinAABB[EntityPlayerMP](entity.getEntityBoundingBox.grow(32D))
          .foreach(addTrackingPlayer)
      }
    }
  }

  /**
    * Deconstruct this [[Phase]].
    */
  def deconstruct(): Unit = {
    _isActive = false

    //Due to us setting spellcardInfo to None here, the remove tracking code never gets called. As such we need to send the packet here
    spellcardInfo.foreach(_.clear())
    spellcardInfo = None
  }

  @LogicalSideOnly(Side.CLIENT)
  def clientUpdate(): Unit = {
    _counter += 1
    if (_counter > interval) _counter = 0
  }

  @LogicalSideOnly(Side.SERVER)
  def serverUpdate(): Unit = {
    _counter += 1
    if (_counter > interval) _counter = 0

    spellcardInfo.foreach(_.tick())

    if (useFreeze && isCounterStart) {
      val target = entity.getAttackTarget

      if (!isFrozen && (target == null || !entity.getEntitySenses.canSee(target))) {
        oldInterval = interval
        interval = 0
      } else if (isFrozen && target != null && entity.getEntitySenses.canSee(target)) {
        interval = oldInterval
        oldInterval = -1
      }
    }
    if (_counter % 10 == 0 || interval < 10) {
      spellcardName.foreach { name =>
        spellcardInfo match {
          case Some(info) => info.setName(name)
          case None =>
            spellcardInfo = Some(new SpellcardInfoServer(name))
        }
      }
    }
  }

  def isActive: Boolean = _isActive

  protected def useFreeze = true

  protected def isFrozen: Boolean = oldInterval != -1

  def addTrackingPlayer(player: EntityPlayerMP): Unit = spellcardInfo.foreach(_.addPlayer(player))

  def removeTrackingPlayer(player: EntityPlayerMP): Unit = spellcardInfo.foreach(_.removePlayer(player))

  /**
    * Check if the counter is zero.
    * If you want to do some action every x ticks, set
    * the interval to x and test for this.
    */
  protected def isCounterStart: Boolean = _counter == 0

  def entity: EntityDanmakuMob = manager.entity

  def getEntity: EntityDanmakuMob = entity

  def phaseType: PhaseType

  /**
    * Check if this is a spellcard. Used to get the amount of stars to show for bosses.
    */
  def isSpellcard: Boolean = spellcard.nonEmpty

  /**
    * Returns the name to render in spellcard like fashion.
    * Doesn't actually need to be a real spellcard.
    */
  def spellcardName: Option[ITextComponent] =
    spellcard.map(card => new TextComponentTranslation(card.unlocalizedName))

  /**
    * If this [[Phase]] represents a spellcard, returns the spellcard.
    */
  def spellcard: Option[Spellcard] = None

  def dropLoot(source: DamageSource): Unit = {}

  override def serializeNBT: NBTTagCompound = {
    val tag = new NBTTagCompound
    tag.setString(Phase.NbtName, phaseType.fullNameString)
    tag.setInteger(NbtCounter, _counter)
    tag.setInteger(NbtInterval, interval)
    tag.setInteger(NbtOldInterval, oldInterval)
    tag
  }

  override def deserializeNBT(nbt: NBTTagCompound): Unit = {
    _counter = nbt.getInteger(NbtCounter)
    interval = nbt.getInteger(NbtInterval)
    oldInterval = nbt.getInteger(NbtOldInterval)
  }
}
abstract class PhaseType extends RegistryValue[PhaseType] {
  def instantiate(phaseManager: PhaseManager): Phase
}
object PhaseType {
  implicit val ordering: Ordering[PhaseType] = Ordering.by((card: PhaseType) => card.fullNameString)
}

//Java-API for Phase
abstract class AbstractPhase(manager: PhaseManager) extends Phase(manager) {

  override def spellcard: Option[Spellcard] = spellcardJ.toOption

  override def spellcardName: Option[ITextComponent] = spellcardNameJ.toOption

  def spellcardJ: Optional[Spellcard] = Optional.empty()

  def spellcardNameJ: Optional[_ <: ITextComponent] =
    spellcard.map((card: Spellcard) => new TextComponentTranslation(card.unlocalizedName)).toOptional
}
