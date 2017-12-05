/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable.ParMap

import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait DanmakuHandler {

  private var _danmaku: Map[Int, DanmakuState] = HashMap.empty[Int, DanmakuState]
  private var newDanmaku     = new ArrayBuffer[DanmakuState]
  private var danmakuChanges = new ArrayBuffer[DanmakuChanges]
  var working: ParMap[Int, DanmakuUpdate] = _

  def danmaku: Iterable[DanmakuState] = _danmaku.values

  def processChange(
      id: Int,
      signal: DanmakuUpdateSignal,
      map: mutable.HashMap[Int, Option[DanmakuState]]
  ): Option[DanmakuState] = {
    map.get(id).orElse(_danmaku.get(id).map(Some.apply))

    map.get(id).orElse(_danmaku.get(id).map(Some.apply)).flatten.flatMap { state =>
      signal match {
        case DanmakuUpdateSignal.ChangedPos(pos)                   => Some(state.copy(pos = pos, prevPos = pos))
        case DanmakuUpdateSignal.ChangedMotion(motion)             => Some(state.copy(motion = motion))
        case DanmakuUpdateSignal.ChangedDirection(direction)       => Some(state.copy(direction = direction))
        case DanmakuUpdateSignal.ChangedOrientation(orientation)   => Some(state.copy(orientation = orientation))
        case DanmakuUpdateSignal.ChangedShotData(shotData)         => Some(state.copy(shot = shotData))
        case DanmakuUpdateSignal.ChangedMovementData(movementData) => Some(state.copy(movement = movementData))
        case DanmakuUpdateSignal.ChangedRotationData(rotationData) => Some(state.copy(rotation = rotationData))
        case DanmakuUpdateSignal.SetDead                           => None
        case DanmakuUpdateSignal.Finish =>
          val shot  = state.shot
          val world = state.world
          val pos   = state.pos

          val target =
            state.user.flatMap(u => Option(u.getLastDamageSource)).flatMap(s => Option(s.getImmediateSource)).collect {
              case living: EntityLivingBase => living
            }

          val launchDirection = target.fold(Vector3.Down)(to => Vector3.directionToEntity(pos, to))
          if (shot.sizeZ > 1F && shot.sizeZ / shot.sizeX > 3 && shot.sizeZ / shot.sizeY > 3) {
            for (zPos <- 0 until shot.sizeZ.toInt) {
              val realPos = pos.offset(launchDirection, zPos)
              world.spawnEntity(TouhouHelper.createScoreGreen(world, target, realPos, launchDirection))
            }
          } else {
            world.spawnEntity(TouhouHelper.createScoreGreen(world, target, pos, launchDirection))
          }

          None
      }
    }
  }

  def start(): Unit = {
    val temp = mutable.HashMap.empty[Int, Option[DanmakuState]]

    //Handle changes
    danmakuChanges.foreach {
      case DanmakuChanges(id, signals) =>
        signals.foreach { signal =>
          temp.put(id, processChange(id, signal, temp))
        }
    }

    val (updates, removes) = temp.partition(_._2.isDefined)
    val updatesWithState   = updates.mapValues(_.get)
    val removeKeys         = removes.keySet

    val mutableDanmaku = mutable.HashMap(_danmaku.toSeq: _*)
    removeKeys.foreach(mutableDanmaku.remove)
    updatesWithState.foreach {
      case (id, state) =>
        mutableDanmaku.put(id, state)
    }

    _danmaku = mutableDanmaku.toMap

    working = _danmaku.par.flatMap(t => t._2.update.map(t => (t.state.id, t))) ++ newDanmaku.par.flatMap(
      _.update.map(t => (t.state.id, t))
    )
    newDanmaku.clear()
  }

  def stop(): Iterable[DanmakuChanges] = {
    val updated = working.seq
    val updates = updated.flatMap {
      case (_, DanmakuUpdate(state, stateUpdates, callbacks)) =>
        callbacks.foreach(_.apply())
        if (stateUpdates.nonEmpty) Some(DanmakuChanges(state.id, stateUpdates)) else None
    }

    _danmaku = updated.map(t => t._1 -> t._2.state)
    working = null

    updates
  }

  def spawnDanmaku(state: DanmakuState): Unit = newDanmaku += state

  def handleDanmakuChange(changes: DanmakuChanges): Unit = danmakuChanges += changes
}

@SideOnly(Side.SERVER)
class ServerDanmakuHandler extends DanmakuHandler {

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

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onTick(event: TickEvent.ClientTickEvent): Unit = {
    if (!Minecraft.getMinecraft.isGamePaused) {
      if (event.phase == Phase.START) {
        start()
      } else if (event.phase == Phase.END) {
        stop()
      }
    }
  }
}
