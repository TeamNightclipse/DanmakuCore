/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable.ParMap
import scala.collection.{immutable, mutable}

import net.katsstuff.danmakucore.network.{DanCorePacketHandler, DanmakuCreatePacket, DanmakuUpdatePacket}
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

  protected var danmaku:              Map[Int, DanmakuState]           = HashMap.empty[Int, DanmakuState]
  protected var newDanmaku:           ArrayBuffer[DanmakuState]        = ArrayBuffer.empty[DanmakuState]
  protected var danmakuChanges:       ArrayBuffer[DanmakuChanges]      = ArrayBuffer.empty[DanmakuChanges]
  protected var forcedDanmakuUpdates: ArrayBuffer[(Int, DanmakuState)] = ArrayBuffer.empty[(Int, DanmakuState)]
  protected val chunkMap: mutable.WeakHashMap[World, mutable.LongMap[DanmakuChunk]] =
    mutable.WeakHashMap.empty[World, mutable.LongMap[DanmakuChunk]]
  protected var isChunkMapPolpulated: Boolean                    = false
  var working:                        ParMap[Int, DanmakuUpdate] = _

  protected def isReady: Boolean = working != null

  protected def updateStates(
      tempMap: mutable.Map[Int, DanmakuState],
      updates: ArrayBuffer[(Int, Option[DanmakuState])]
  ): Unit = {
    updates.foreach {
      case (id, Some(state)) => tempMap.put(id, state)
      case (id, None)        => tempMap.remove(id)
    }
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
    processSignalsAndForcedDanmaku(danmakuChanges.flatMap(c => danmaku.get(c.id).map(_ -> c.signals)))

    profiler.endStartSection("startUpdates")

    working = danmaku.par.mapValues(_.update).toMap ++ newDanmaku.par.map(state => state.id -> state.update).toMap

    danmakuChanges.clear()
    newDanmaku.clear()
    forcedDanmakuUpdates.clear()
    profiler.endSection()
  }

  protected def stop(): Unit = {
    profiler.startSection("danmaku")
    profiler.startSection("gatherUpdates")
    val updated = working.seq

    profiler.endStartSection("processCallbacks")
    danmakuChanges ++= updated.flatMap {
      case (_, DanmakuUpdate(optState, stateUpdates, callbacks)) =>
        callbacks.foreach(_.apply())
        optState.map(state => DanmakuChanges(state.id, stateUpdates))
    }

    danmaku = updated.flatMap(t => t._2.state.map(state => t._1 -> state))
    working = null
    chunkMap.clear()
    isChunkMapPolpulated = false

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

  def collectDanmakuInAABB[A](world: World, aabb: AxisAlignedBB)(f: PartialFunction[DanmakuState, A]): immutable.IndexedSeq[A] = {
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
  private val removedPlayers = mutable.ArrayBuffer.empty[EntityPlayerMP]

  override def spawnDanmaku(states: Seq[DanmakuState]): Unit = {
    val newDanmakuMap = mutable.Map.empty[EntityPlayerMP, mutable.Buffer[DanmakuState]]
    val newStates = states.map { danmaku =>
      val newTracking = danmaku.updatePlayerEntities(danmaku.world.playerEntities.asScala.collect {
        case playerMP: EntityPlayerMP => playerMP
      })

      newTracking.trackingPlayers.foreach { player =>
        newDanmakuMap.getOrElseUpdate(player, mutable.Buffer.empty) += danmaku
      }

      danmaku.copy(tracking = newTracking)
    }

    DanCorePacketHandler.sendToAll(DanmakuCreatePacket(newStates))

    /*
    newDanmakuMap.foreach {
      case (player, playerStates) =>
        DanCorePacketHandler.sendTo(DanmakuCreatePacket(playerStates), player)
    }
     */

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
      case (player, changes) =>
        DanCorePacketHandler.sendTo(DanmakuUpdatePacket(changes), player)

    }

    super.processSignalsAndForcedDanmaku(stateToSignals)
  }

  def removePlayer(playerMP: EntityPlayerMP): Unit = removedPlayers += playerMP

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onTick(event: TickEvent.ServerTickEvent): Unit = {
    if (event.phase == Phase.START) {
      danmaku = danmaku.transform {
        case (_, state) =>
          val newTracking = state.updatePlayerList(state.world.playerEntities.asScala.collect {
            case playerMP: EntityPlayerMP => playerMP
          })

          removedPlayers.foreach { player =>
            if (newTracking.trackingPlayers.contains(player)) {
              danmakuChanges += DanmakuChanges(state.id, Seq(SetDeadDanmaku()))
            }
          }

          state.copy(tracking = newTracking.copy(trackingPlayers = newTracking.trackingPlayers -- removedPlayers))
      }

      removedPlayers.clear()

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
