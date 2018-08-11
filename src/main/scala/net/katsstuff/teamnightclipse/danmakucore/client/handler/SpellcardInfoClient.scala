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
package net.katsstuff.teamnightclipse.danmakucore.client.handler

import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.spellcardbar.SpellcardInfo
import net.katsstuff.teamnightclipse.danmakucore.network.{
  AddSpellcardInfo,
  SetSpellcardInfoMirror,
  SetSpellcardInfoName,
  SetSpellcardInfoPos,
  SpellcardInfoPacket
}
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class SpellcardInfoClient(val message: AddSpellcardInfo)
    extends SpellcardInfo(message.uuid, message.name, message.mirror) {
  private var _posX: Float = message.posX
  private var _posY: Float = message.posY

  private var _prevPosX: Float = _posX
  private var _prevPosY: Float = _posY

  def getPosX: Float = _posX
  def setPosX(posX: Float): Unit = {
    _prevPosX = posX
    this._posX = posX
  }

  override def getPosY: Float = _posY
  def setPosY(posY: Float): Unit = {
    _prevPosY = posY
    this._posY = posY
  }

  def getRenderPosX(res: ScaledResolution, partialTicks: Float): Int =
    ((_prevPosX + (_posX - _prevPosX) * partialTicks) * res.getScaledWidth).toInt
  def getRenderPosY(res: ScaledResolution, partialTicks: Float): Int =
    ((_prevPosY + (_posY - _prevPosY) * partialTicks) * res.getScaledHeight).toInt

  private[danmakucore] def handlePacket(message: SpellcardInfoPacket): Unit = {
    message match {
      case SetSpellcardInfoName(_, name)     => setName(name)
      case SetSpellcardInfoMirror(_, mirror) => setMirrorText(mirror)
      case SetSpellcardInfoPos(_, newPosX, newPosY, false) =>
        this._prevPosX = _posX
        this._prevPosY = _posY
        this._posX = newPosX
        this._posY = newPosY
      case SetSpellcardInfoPos(_, newPosX, newPosY, true) =>
        this._posX = newPosX
        this._posY = newPosY
        this._prevPosX = _posX
        this._prevPosY = _posY
      case _ => //Ignore
    }
  }
}
