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

import scala.collection.mutable.ArrayBuffer

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.entity.living.boss.EntityDanmakuBoss
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler
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
    val mc       = Minecraft.getMinecraft
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
          } else {
            for (j <- 0 until remainingSpellcards) {
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
