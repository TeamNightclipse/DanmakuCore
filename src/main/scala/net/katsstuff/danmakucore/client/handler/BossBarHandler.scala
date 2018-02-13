/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler

import scala.collection.mutable.ArrayBuffer

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.entity.living.boss.EntityDanmakuBoss
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class BossBarHandler {
  private val StarLocation = DanmakuCore.resource("textures/gui/boss/star.png")

  final val danmakuBosses = new ArrayBuffer[EntityDanmakuBoss]

  @SubscribeEvent
  def onBossBar(event: RenderGameOverlayEvent.BossInfo): Unit = {
    val bossInfo = event.getBossInfo
    val mc = Minecraft.getMinecraft
    danmakuBosses.foreach { boss =>
      if (bossInfo.getUniqueId == boss.getBossInfoUUID) {
        GlStateManager.pushMatrix()

        val baseX = 5
        val baseY = event.getY - 7

        val remainingSpellcards = boss.remainingSpellcards
        if (remainingSpellcards > 0) {
          mc.getTextureManager.bindTexture(StarLocation)
          if (remainingSpellcards > 8) {
            Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0F, 0F, 12, 12, 12F, 12F)
            mc.fontRenderer.drawStringWithShadow("x" + remainingSpellcards, baseX + 14F, baseY + 4F, 0xFFFFFF)
          }
          else {
            for(j <- 0 until remainingSpellcards) {
              Gui.drawModalRectWithCustomSizedTexture(baseX + (j * 14), baseY, 0F, 0F, 12, 12, 12F, 12F)
            }
          }
        }

        if (ConfigHandler.client.entities.circleBossBar) {
          event.setCanceled(true)
          //TODO
        }

        GlStateManager.popMatrix()
      }
    }
  }
}