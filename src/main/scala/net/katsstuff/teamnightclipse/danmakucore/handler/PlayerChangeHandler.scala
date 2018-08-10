/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.handler

import scala.collection.mutable

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object PlayerChangeHandler {
  private val changes = new mutable.WeakHashMap[EntityPlayer, Array[Double]]
  private val PrevX   = 0
  private val PrevY   = 1
  private val PrevZ   = 2
  private val X       = 3
  private val Y       = 4
  private val Z       = 5

  @SubscribeEvent
  def onPlayerTick(event: TickEvent.PlayerTickEvent): Unit = {
    val player = event.player
    if (event.phase == TickEvent.Phase.START && !player.world.isRemote) {
      val change = changes.get(player) match {
        case Some(array) =>
          array(PrevX) = array(X)
          array(PrevY) = array(Y)
          array(PrevZ) = array(Z)
          array(X) = player.posX
          array(Y) = player.posY
          array(Z) = player.posZ
          array
        case None => Array(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ)
      }
      changes.put(player, change)
    }
  }

  def getPlayerChange(player: EntityPlayer): Array[Double] =
    changes.get(player) match {
      case Some(change) => Array(change(X) - change(PrevX), change(Y) - change(PrevY), change(Z) - change(PrevZ))
      case None         => Array(0D, 0D, 0D)
    }
}
