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
package net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata

import net.katsstuff.teamnightclipse.danmakucore.network.{DanCoreDataPacket, DanCorePacketHandler}
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.TargetPoint
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP

trait IDanmakuCoreData {
  def power: Float
  def power_=(newPower: Float): Unit

  def getPower: Float                 = power
  def setPower(newPower: Float): Unit = power = newPower
  def addPower(newPower: Float): Unit = power += newPower

  def score: Int
  def score_=(newScore: Int): Unit

  def getScore: Int                 = score
  def setScore(newScore: Int): Unit = score = newScore
  def addScore(newScore: Int): Unit = score += newScore

  def lives: Int
  def lives_=(newLives: Int): Unit

  def getLives: Int                 = lives
  def setLives(newLives: Int): Unit = lives = newLives
  def addLives(newLives: Int): Unit = lives += newLives
  def addLife(): Unit               = addLives(1)
  def removeLife(): Unit            = addLives(-1)

  def bombs: Int
  def bombs_=(newBombs: Int): Unit

  def getBombs: Int                 = bombs
  def setBombs(newBombs: Int): Unit = bombs = newBombs
  def addBombs(newBombs: Int): Unit = bombs += newBombs
  def addBomb(): Unit               = addBombs(1)
  def removeBomb(): Unit            = addBombs(-1)

  def syncTo(playerMP: EntityPlayerMP, target: Entity): Unit =
    DanCorePacketHandler.sendTo(new DanCoreDataPacket(this, target), playerMP)
  def syncToClose(point: TargetPoint, target: Entity): Unit =
    DanCorePacketHandler.sendToAllAround(new DanCoreDataPacket(this, target), point)
}
