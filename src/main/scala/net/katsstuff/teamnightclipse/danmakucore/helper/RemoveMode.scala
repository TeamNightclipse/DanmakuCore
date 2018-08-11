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
package net.katsstuff.teamnightclipse.danmakucore.helper

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer

sealed trait RemoveMode {
  def shouldRemove(state: DanmakuState, centerEntity: Entity): Boolean
}
object RemoveMode {

  /**
    * Remove all danmaku.
    */
  object All extends RemoveMode {
    override def shouldRemove(state: DanmakuState, centerEntity: Entity): Boolean = true
  }

  /**
    * Remove all danmaku.
    */
  def all: RemoveMode = All

  /**
    * Removes all danmaku except that which is created by players.
    */
  object Enemy extends RemoveMode {
    override def shouldRemove(state: DanmakuState, centerEntity: Entity): Boolean =
      !state.user.exists(_.isInstanceOf[EntityPlayer])
  }

  /**
    * Removes all danmaku except that which is created by players.
    */
  def enemy: RemoveMode = Enemy

  /**
    * Removes all danmaku created by players.
    */
  object Player extends RemoveMode {
    override def shouldRemove(state: DanmakuState, centerEntity: Entity): Boolean =
      state.user.exists(_.isInstanceOf[EntityPlayer])
  }

  /**
    * Removes all danmaku created by players.
    */
  def player: RemoveMode = Player

  /**
    * Remove all danmaku that wasn't created by the user.
    */
  object Other extends RemoveMode {
    override def shouldRemove(state: DanmakuState, centerEntity: Entity): Boolean = state.user.exists(_ != centerEntity)
  }

  /**
    * Remove all danmaku that wasn't created by the user.
    */
  def other: RemoveMode = Other
}
