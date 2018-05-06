/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper

import net.katsstuff.danmakucore.danmaku.DanmakuState
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
