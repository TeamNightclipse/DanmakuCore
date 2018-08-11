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
package net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.spellcardbar

import java.util.UUID

import scala.collection.mutable.ArrayBuffer

import net.katsstuff.teamnightclipse.danmakucore.network._
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.ITextComponent

class SpellcardInfoServer(uuid: UUID, name: ITextComponent, mirrorText: Boolean)
    extends SpellcardInfo(uuid, name, mirrorText) {
  final val DestinationHeight = 0.02F

  private var position = 1F
  private val sentTo   = new ArrayBuffer[EntityPlayerMP]

  def this(name: ITextComponent) {
    this(MathHelper.getRandomUUID, name, true)
  }

  def setNoAnimation(): Unit = {
    position = DestinationHeight
    sendPacketAll(SetSpellcardInfoPos(uuid, getPosX, getPosY, absolute = true))
  }

  def tick(): Unit = if (position > DestinationHeight) {
    position = (position - 0.35F * getAcceleration).toFloat
    if (position < DestinationHeight) {
      position = DestinationHeight
      sendPacketAll(SetSpellcardInfoPos(uuid, getPosX, getPosY, absolute = true))
    } else sendPacketAll(SetSpellcardInfoPos(uuid, getPosX, getPosY, absolute = false))
  }

  override def setName(name: ITextComponent): Unit = {
    super.setName(name)
    sendPacketAll(SetSpellcardInfoName(uuid, name))
  }

  private def getAcceleration = (position - 1F) * -1 + 0.01
  override def setMirrorText(mirrorText: Boolean): Unit = {
    super.setMirrorText(mirrorText)
    sendPacketAll(SetSpellcardInfoMirror(uuid, mirrorText))
  }

  override def getPosX        = 1F
  override def getPosY: Float = position
  def addPlayer(player: EntityPlayerMP): Unit = {
    sentTo += player
    DanCorePacketHandler.sendTo(AddSpellcardInfo(uuid, name, mirrorText, getPosX, getPosY), player)
  }

  def removePlayer(player: EntityPlayerMP): Unit = {
    sentTo -= player
    DanCorePacketHandler.sendTo(RemoveSpellcardInfo(uuid), player)
  }

  def clear(): Unit = {
    sendPacketAll(RemoveSpellcardInfo(uuid))
    sentTo.clear()
  }

  private def sendPacketAll(packet: SpellcardInfoPacket): Unit =
    sentTo.foreach(p => DanCorePacketHandler.sendTo(packet, p))
}
