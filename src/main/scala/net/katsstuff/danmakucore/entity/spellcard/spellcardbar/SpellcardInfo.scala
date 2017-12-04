/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard.spellcardbar

import java.util.UUID

import net.minecraft.util.text.ITextComponent

abstract class SpellcardInfo(val uuid: UUID, var name: ITextComponent, var mirrorText: Boolean) {
  def getUuid: UUID = uuid

  def getName:                       ITextComponent = name
  def setName(name: ITextComponent): Unit           = this.name = name

  def shouldMirrorText:                   Boolean = mirrorText
  def setMirrorText(mirrorText: Boolean): Unit    = this.mirrorText = mirrorText

  def getPosX: Float
  def getPosY: Float
}
