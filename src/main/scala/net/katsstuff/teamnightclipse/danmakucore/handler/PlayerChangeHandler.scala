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
