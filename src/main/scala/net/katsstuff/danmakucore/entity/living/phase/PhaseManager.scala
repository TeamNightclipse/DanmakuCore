/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.phase

import java.util

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob
import net.katsstuff.danmakucore.lib.data.LibPhases
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.{Constants, INBTSerializable}

class PhaseManager(val entity: EntityDanmakuMob) extends INBTSerializable[NBTTagCompound] {
  private val NBT_PHASES = "phases"
  private val NBT_INDEX  = "index"

  private val _phaseList         = new ArrayBuffer[Phase]
  private var _currentPhaseIndex = 0

  /**
    * Ticks the current phase.
    */
  def tick(): Unit = {
    val phase = currentPhase
    if (phase.isActive)
      if (!entity.world.isRemote) phase.serverUpdate()
      else phase.clientUpdate()
  }

  /**
    * The phase that is currently in use.
    */
  def currentPhase: Phase = _phaseList(_currentPhaseIndex)

  /**
    * Sets a phase as the one in use, overriding the previous one. This also initiates the new phase.
    */
  def setCurrentPhase(phase: Phase): Unit = {
    currentPhase.deconstruct()
    _phaseList.update(_currentPhaseIndex, phase)
    phase.init()
  }

  /**
    * Sets the current phase to the next one and initiates it.
    */
  def nextPhase(): Unit = {
    currentPhase.deconstruct()
    _currentPhaseIndex += 1
    currentPhase.init()
  }

  /**
    * Sets the current phase to the previous one and initiates it.
    */
  def previousPhase(): Unit = {
    currentPhase.deconstruct()
    _currentPhaseIndex -= 1
    currentPhase.init()
  }

  /**
    * Adds a new phase.
    */
  def addPhase(phase: Phase): Unit = _phaseList += phase

  /**
    * Adds new phases.
    */
  def addPhases(phases: Seq[Phase]): Unit = _phaseList ++= phases

  /**
    * Adds new phases.
    */
  def addPhases(phases: util.List[Phase]): Unit = addPhases(phases.asScala)

  /**
    * Sets a phase at the specific index.
    */
  def setPhase(phase: Phase, index: Int): Unit = _phaseList(index) = phase

  /**
    * Removes a phase.
    */
  def removePhase(phase: Phase): Unit = _phaseList -= phase
  def removePhase(index: Int): Unit   = _phaseList.remove(index)

  /**
    * Gets the index of a phase.
    */
  def phaseIndexOf(phase: Phase): Int = _phaseList.indexOf(phase)

  /**
    * Changes the current active phase without throwing away the previous one.
    */
  def changePhase(newPhase: Int): Unit = _currentPhaseIndex = newPhase

  def hasNextPhase: Boolean = _currentPhaseIndex < _phaseList.size - 1

  def phaseList: Seq[Phase] = _phaseList.toIndexedSeq

  def getPhaseList: util.List[Phase] = phaseList.asJava

  def currentPhaseIndex: Int = _currentPhaseIndex

  def getCurrentPhaseInex: Int = currentPhaseIndex

  override def serializeNBT: NBTTagCompound = {
    val tag = new NBTTagCompound
    tag.setInteger(NBT_INDEX, _currentPhaseIndex)

    val list = new NBTTagList
    _phaseList.foreach(phase => list.appendTag(phase.serializeNBT))
    tag.setTag(NBT_PHASES, list)

    tag
  }

  override def deserializeNBT(tag: NBTTagCompound): Unit = {
    _currentPhaseIndex = tag.getInteger(NBT_INDEX)

    val list = tag.getTagList(NBT_PHASES, Constants.NBT.TAG_COMPOUND)
    val size = list.tagCount

    val multiMap = _phaseList.groupBy(_.phaseType).mapValues(_.toSeq)

    val deserialized = (0 until size)
      .foldLeft((multiMap, Seq.empty[Phase])) {
        case ((map, acc), i) =>
          val tagPhase = list.getCompoundTagAt(i)
          val phaseType =
            Option(DanmakuRegistry.Phase.getValue(new ResourceLocation(tagPhase.getString(Phase.NbtName))))
              .getOrElse(LibPhases.FALLBACK)

          val (phase, next) = map
            .get(phaseType)
            .map(existing => (existing.head, existing.tail))
            .getOrElse((phaseType.instantiate(this), Nil))

          phase.deserializeNBT(tagPhase)

          if (next.isEmpty) {
            (map - phaseType, acc :+ phase)
          } else {
            (map.updated(phaseType, next), acc :+ phase)
          }
      }
      ._2

    _phaseList.clear()
    _phaseList ++= deserialized
    if (_currentPhaseIndex != 0 && _phaseList(0).isActive) _phaseList(0).deconstruct()
    currentPhase.init()
  }
}
