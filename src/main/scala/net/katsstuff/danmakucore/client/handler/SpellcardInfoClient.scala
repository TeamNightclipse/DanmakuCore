/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler

import net.katsstuff.danmakucore.entity.spellcard.spellcardbar.SpellcardInfo
import net.katsstuff.danmakucore.network.{AddSpellcardInfo, SetSpellcardInfoMirror, SetSpellcardInfoName, SetSpellcardInfoPos, SpellcardInfoPacket}
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class SpellcardInfoClient(val message: AddSpellcardInfo) extends SpellcardInfo(message.uuid, message.name, message.mirror) {
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
    ((_prevPosX + (_posX - _prevPosX) * partialTicks) *
      res.getScaledWidth).toInt
  def getRenderPosY(res: ScaledResolution, partialTicks: Float): Int =
    ((_prevPosY + (_posY - _prevPosY) * partialTicks) *
      res.getScaledHeight).toInt

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
