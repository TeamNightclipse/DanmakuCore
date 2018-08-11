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

import java.util.UUID

import scala.collection.mutable

import net.katsstuff.teamnightclipse.danmakucore.network.{AddSpellcardInfo, RemoveSpellcardInfo, SpellcardInfoPacket}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
private[danmakucore] class SpellcardHandler {

  private val spellcards = new mutable.HashMap[UUID, SpellcardInfoClient]
  @SubscribeEvent
  def onLeave(event: PlayerEvent.PlayerLoggedOutEvent): Unit = spellcards.clear()

  @SubscribeEvent
  def renderSpellcard(event: RenderGameOverlayEvent.Post): Unit = {
    val mc      = Minecraft.getMinecraft
    val partial = event.getPartialTicks
    val res     = event.getResolution

    val red        = 0xFFFF0000
    val fontHeight = mc.fontRenderer.FONT_HEIGHT

    GlStateManager.pushMatrix()

    val seq = spellcards.values.toSeq
    for (i <- seq.indices) {
      val info    = seq(i)
      val renderX = info.getRenderPosX(res, partial)
      val renderY = info.getRenderPosY(res, partial) + i * 16

      val formattedText = info.getName.getFormattedText
      val stringWidth   = mc.fontRenderer.getStringWidth(formattedText)

      if (info.shouldMirrorText) {
        mc.fontRenderer.drawStringWithShadow(formattedText, renderX - stringWidth.toFloat, renderY, 0xFFFFFF)
        Gui.drawRect(renderX - stringWidth, renderY + fontHeight, renderX - 2, renderY + fontHeight + 1, red)
        Gui.drawRect(renderX - stringWidth + 40, renderY + 2 + fontHeight, renderX - 2, renderY + fontHeight + 3, red)
      } else {
        mc.fontRenderer.drawStringWithShadow(formattedText, renderX, renderY, 0xFFFFFF)
        Gui.drawRect(renderX, renderY + fontHeight, renderX + stringWidth - 2, renderY + fontHeight + 1, red)
        Gui.drawRect(renderX, renderY + 2 + fontHeight, renderX + stringWidth - 42, renderY + fontHeight + 3, red)
      }
    }
    GlStateManager.popMatrix()
  }

  def handlePacket(packet: SpellcardInfoPacket): Unit = {
    packet match {
      case add: AddSpellcardInfo =>
        spellcards.put(add.uuid, new SpellcardInfoClient(add))
      case RemoveSpellcardInfo(uuid) =>
        spellcards.remove(uuid)
      case other => spellcards.get(other.uuid).foreach(_.handlePacket(other))
    }
  }
}
