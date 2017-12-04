/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler

import java.util.UUID

import scala.collection.mutable

import net.katsstuff.danmakucore.network.{AddSpellcardInfo, RemoveSpellcardInfo, SpellcardInfoPacket}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class SpellcardHandler {

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
    for(i <- seq.indices) {
      val info = seq(i)
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

  private[danmakucore] def handlePacket(packet: SpellcardInfoPacket): Unit = {
    packet match {
      case add: AddSpellcardInfo =>
        spellcards.put(add.uuid, new SpellcardInfoClient(add))
      case RemoveSpellcardInfo(uuid) =>
        spellcards.remove(uuid)
      case other => spellcards.get(other.uuid).foreach(_.handlePacket(other))
    }
  }
}
