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
package net.katsstuff.teamnightclipse.danmakucore.danmaku

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable.ParMap
import scala.collection.{immutable, mutable}

import net.katsstuff.teamnightclipse.danmakucore.network.{DanCorePacketHandler, DanmakuCreatePacket, DanmakuUpdatePacket}
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.profiler.Profiler
import net.minecraft.util.math.{AxisAlignedBB, ChunkPos, MathHelper}
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object DanmakuHandler {

  /**
    * Specifies the maximum size of a danmaku when finding all danmaku in an area.
    * Change this if it's less than your max danmaku size.
    */
  var maxDanmakuRadius: Float = 3F
}
class DanmakuChunk {

  private val danmakuList: Vector[ArrayBuffer[DanmakuState]] = Vector.fill(16)(ArrayBuffer.empty)

  def collectDanmakuInAABB[A](aabb: AxisAlignedBB)(f: PartialFunction[DanmakuState, A]): immutable.IndexedSeq[A] = {
    val maxRadius = DanmakuHandler.maxDanmakuRadius

    val minY = MathHelper.clamp(MathHelper.floor((aabb.minY - maxRadius) / 16D), 0, danmakuList.length - 1)
    val maxY = MathHelper.clamp(MathHelper.floor((aabb.maxY + maxRadius) / 16D), 0, danmakuList.length - 1)

    val res = for {
      y <- minY to maxY
      if danmakuList(y).nonEmpty
      danmaku <- danmakuList(y)
      if danmaku.encompassingAABB.intersects(aabb)
      if danmaku.boundingBoxes.exists(_.intersects(aabb))
    } yield danmaku

    res.collect(f)
  }

  def addDanmaku(danmaku: DanmakuState): Unit = {
    val y = MathHelper.clamp(danmaku.pos.y / 16D, 0, danmakuList.length - 1).toInt
    danmakuList(y) += danmaku
  }
}

trait DanmakuHandler {

  def profiler: Profiler

  protected var danmaku: Map[Int, DanmakuState]                        = HashMap.empty[Int, DanmakuState]
  protected var newDanmaku: ArrayBuffer[DanmakuState]                  = ArrayBuffer.empty[DanmakuState]
  protected var danmakuChanges: ArrayBuffer[DanmakuChanges]            = ArrayBuffer.empty[DanmakuChanges]
  protected var forcedDanmakuUpdates: ArrayBuffer[(Int, DanmakuState)] = ArrayBuffer.empty[(Int, DanmakuState)]
  protected val chunkMap: mutable.WeakHashMap[World, mutable.LongMap[DanmakuChunk]] =
    mutable.WeakHashMap.empty[World, mutable.LongMap[DanmakuChunk]]
  protected var isChunkMapPolpulated: Boolean = false
  var working: ParMap[Int, DanmakuUpdate]     = _

  protected def isReady: Boolean = working != null

  protected def updateStates(
      tempMap: mutable.Map[Int, DanmakuState],
      updates: ArrayBuffer[(Int, Option[DanmakuState])]
  ): Unit =
    updates.foreach {
      case (id, Some(state)) => tempMap.put(id, state)
      case (id, None)        => tempMap.remove(id)
    }

  protected def processSignalsAndForcedDanmaku(
      stateToSignals: ArrayBuffer[(DanmakuState, Seq[DanmakuUpdateSignal])]
  ): Unit = {
    val processedSignals = stateToSignals.map {
      case (state, signals) =>
        state.id -> signals.foldLeft[Option[DanmakuState]](Some(state)) {
          case (Some(currentState), signal) => signal.process(currentState)
          case (None, _)                    => None
        }
    }

    val mutableDanmaku = mutable.HashMap(danmaku.toSeq: _*)
    updateStates(mutableDanmaku, processedSignals)

    forcedDanmakuUpdates.foreach {
      case (id, state) => mutableDanmaku.put(id, state)
    }

    danmaku = mutableDanmaku.toMap
  }

  protected def start(): Unit = {
    profiler.startSection("danmaku")
    profiler.startSection("processChanges")

    if (danmakuChanges.nonEmpty || forcedDanmakuUpdates.nonEmpty) {
      processSignalsAndForcedDanmaku(danmakuChanges.flatMap(c => danmaku.get(c.id).map(_ -> c.signals)))
      danmakuChanges.clear()
      forcedDanmakuUpdates.clear()
    }

    profiler.endStartSection("startUpdates")

    if (danmaku.nonEmpty || newDanmaku.nonEmpty) {
      working = danmaku.par.mapValues(_.update).toMap ++ newDanmaku.par.map(state => state.id -> state.update).toMap
      newDanmaku.clear()
    }

    profiler.endSection()
    profiler.endSection()
  }

  protected def stop(): Unit = {
    profiler.startSection("danmaku")
    profiler.startSection("gatherUpdates")

    if (working != null) {
      val updated = working.seq

      profiler.endStartSection("processCallbacks")
      danmakuChanges ++= updated.flatMap {
        case (_, DanmakuUpdate(optState, stateUpdates, callbacks)) =>
          if(callbacks.nonEmpty) {
            callbacks.foreach(_.apply())
          }

          if (stateUpdates.nonEmpty) optState.map(state => DanmakuChanges(state.id, stateUpdates))
          else Nil
      }

      danmaku = updated.flatMap(t => t._2.state.map(state => t._1 -> state))
      working = null
      chunkMap.clear()
      isChunkMapPolpulated = false
    }

    profiler.endSection()
    profiler.endSection()
  }

  protected def populateChunkMap(): Unit = {
    danmaku.values.foreach { danmaku =>
      val chunk = chunkMap
        .getOrElseUpdate(danmaku.world, mutable.LongMap.empty)
        .getOrElseUpdate(ChunkPos.asLong(danmaku.chunkPosX, danmaku.chunkPosZ), new DanmakuChunk)
      chunk.addDanmaku(danmaku)
    }
  }

  def spawnDanmaku(states: Seq[DanmakuState]): Unit = newDanmaku ++= states

  def addDanmakuChange(changes: DanmakuChanges): Unit = danmakuChanges += changes

  def allDanmaku: Iterable[DanmakuState] = danmaku.values

  def collectDanmakuInAABB[A](world: World, aabb: AxisAlignedBB)(
      f: PartialFunction[DanmakuState, A]
  ): immutable.IndexedSeq[A] = {
    if (!isChunkMapPolpulated) {
      populateChunkMap()
    }

    val maxRadius = DanmakuHandler.maxDanmakuRadius

    val minX = MathHelper.floor((aabb.minX - maxRadius) / 16.0D)
    val maxX = MathHelper.ceil((aabb.maxX + maxRadius) / 16.0D)
    val minZ = MathHelper.floor((aabb.minZ - maxRadius) / 16.0D)
    val maxZ = MathHelper.ceil((aabb.maxZ + maxRadius) / 16.0D)

    for {
      x        <- minX until maxX
      z        <- minZ until maxZ
      chunkMap <- danmakuChunkAt(world, x, z).toSeq
      danmaku  <- chunkMap.collectDanmakuInAABB(aabb)(f)
    } yield danmaku
  }

  protected def danmakuChunkAt(world: World, x: Int, z: Int): Option[DanmakuChunk] =
    chunkMap.get(world).flatMap(_.get(ChunkPos.asLong(x, z)))

  def forceUpdateDanmaku(state: DanmakuState): Unit = forcedDanmakuUpdates += ((state.id, state))

  def updateDanmaku(changes: DanmakuChanges): Unit = danmakuChanges += changes
}

class ServerDanmakuHandler extends DanmakuHandler {

  val profiler: Profiler = FMLCommonHandler.instance().getMinecraftServerInstance.profiler

  override def spawnDanmaku(states: Seq[DanmakuState]): Unit = {
    val newDanmakuMap = mutable.Map.empty[EntityPlayerMP, mutable.Buffer[DanmakuState]]
    val newStates = states.map { danmaku =>
      val newTrackingPlayers = danmaku.newTrackingPlayers(danmaku.world.playerEntities.asScala.collect {
        case playerMP: EntityPlayerMP => playerMP
      })

      newTrackingPlayers.foreach { player =>
        newDanmakuMap.getOrElseUpdate(player, mutable.Buffer.empty) += danmaku
      }

      danmaku.copy(tracking = danmaku.tracking.copy(trackingPlayers = newTrackingPlayers))
    }

    newDanmakuMap.foreach {
      case (player, playerStates) =>
        DanCorePacketHandler.sendTo(DanmakuCreatePacket(playerStates), player)
    }

    super.spawnDanmaku(newStates)
  }

  override protected def processSignalsAndForcedDanmaku(
      stateToSignals: ArrayBuffer[(DanmakuState, Seq[DanmakuUpdateSignal])]
  ): Unit = {
    val danmakuChangesMap = mutable.Map.empty[EntityPlayerMP, mutable.Buffer[DanmakuChanges]]

    stateToSignals.foreach {
      case (state, signals) =>
        state.tracking.trackingPlayers.foreach { player =>
          danmakuChangesMap.getOrElseUpdate(player, mutable.Buffer.empty) += DanmakuChanges(state.id, signals)
        }
    }

    danmakuChangesMap.foreach {
      case (player, changes) => DanCorePacketHandler.sendTo(DanmakuUpdatePacket(changes), player)
    }

    super.processSignalsAndForcedDanmaku(stateToSignals)
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onTick(event: TickEvent.ServerTickEvent): Unit = {
    if (event.phase == Phase.START) {
      start()
    } else if (event.phase == Phase.END) {
      stop()
    }
  }
}

@SideOnly(Side.CLIENT)
class ClientDanmakuHandler extends DanmakuHandler {

  val profiler: Profiler = Minecraft.getMinecraft.mcProfiler

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onTick(event: TickEvent.ClientTickEvent): Unit = {
    val integrated = Minecraft.getMinecraft.isIntegratedServerRunning
    if (integrated && !Minecraft.getMinecraft.isGamePaused || !integrated) {
      if (event.phase == Phase.START) {
        start()
      } else if (event.phase == Phase.END && isReady) {
        stop()
      }
    }
  }
}
