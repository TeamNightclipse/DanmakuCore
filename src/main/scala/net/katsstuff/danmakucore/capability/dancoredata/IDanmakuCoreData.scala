/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability.dancoredata

import net.katsstuff.danmakucore.network.{DanCoreDataPacket, DanCorePacketHandler}
import net.katsstuff.mirror.network.scalachannel.TargetPoint
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP

trait IDanmakuCoreData {
  def power:                    Float
  def power_=(newPower: Float): Unit

  def getPower:                  Float = power
  def setPower(newPower: Float): Unit  = power = newPower
  def addPower(newPower: Float): Unit  = power += newPower

  def score:                  Int
  def score_=(newScore: Int): Unit

  def getScore:                Int  = score
  def setScore(newScore: Int): Unit = score = newScore
  def addScore(newScore: Int): Unit = score += newScore

  def lives:                  Int
  def lives_=(newLives: Int): Unit

  def getLives:                Int  = lives
  def setLives(newLives: Int): Unit = lives = newLives
  def addLives(newLives: Int): Unit = lives += newLives
  def addLife():               Unit = addLives(1)
  def removeLife():            Unit = addLives(-1)

  def bombs:                  Int
  def bombs_=(newBombs: Int): Unit

  def getBombs:                Int  = bombs
  def setBombs(newBombs: Int): Unit = bombs = newBombs
  def addBombs(newBombs: Int): Unit = bombs += newBombs
  def addBomb():               Unit = addBombs(1)
  def removeBomb():            Unit = addBombs(-1)

  def syncTo(playerMP: EntityPlayerMP, target: Entity): Unit =
    DanCorePacketHandler.sendTo(new DanCoreDataPacket(this, target), playerMP)
  def syncToClose(point: TargetPoint, target: Entity): Unit =
    DanCorePacketHandler.sendToAllAround(new DanCoreDataPacket(this, target), point)
}
