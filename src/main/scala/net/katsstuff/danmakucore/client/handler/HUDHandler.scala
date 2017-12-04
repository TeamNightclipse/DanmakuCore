/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.capability.IDanmakuCoreData
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.katsstuff.danmakucore.helper.MathUtil._
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{Gui, ScaledResolution}
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT) class HUDHandler {
  private var powerBarVisible = 1F
  private var starsVisible    = 1F

  private val LifeFull        = DanmakuCore.resource("textures/gui/hud/life_full.png")
  private val LifeEmpty       = DanmakuCore.resource("textures/gui/hud/life_empty.png")
  private val BombFull        = DanmakuCore.resource("textures/gui/hud/bomb_full.png")
  private val BombEmpty       = DanmakuCore.resource("textures/gui/hud/bomb_empty.png")
  private val Power           = DanmakuCore.resource("textures/gui/hud/power.png")
  private val PowerBackground = DanmakuCore.resource("textures/gui/hud/power_background.png")

  @SubscribeEvent
  def onDraw(event: RenderGameOverlayEvent.Post): Unit = if (event.getType eq RenderGameOverlayEvent.ElementType.ALL) {
    val mc = Minecraft.getMinecraft
    TouhouHelper.getDanmakuCoreData(mc.player).foreach { data =>
      val res = event.getResolution

      GlStateManager.pushMatrix()

      drawPower(res, mc, data.getPower, data.getScore)
      drawLivesAndBombs(res, mc, data)

      GlStateManager.popMatrix()
    }
  }

  private def drawPower(res: ScaledResolution, mc: Minecraft, power: Float, score: Int) = {
    val filled     = if (power > 4F) 1F else power / 4F
    val widthEnd   = ConfigHandler.hud.power.widthEnd
    val widthStart = ConfigHandler.hud.power.widthStart

    val height = 10

    val widthUsed   = widthEnd - widthStart
    val widthFilled = (widthUsed * filled).toInt

    val powerPosX = res.getScaledWidth + ConfigHandler.hud.power.posX
    val powerPosY = res.getScaledHeight - ConfigHandler.hud.power.posY

    val powerStartPosX  = powerPosX - widthEnd
    val powerFilledPosX = powerPosX - widthFilled - widthStart

    powerBarVisible = fade(ConfigHandler.hud.power.hideIfFull, filled ==~ 1F, powerBarVisible)

    mc.getTextureManager.bindTexture(PowerBackground)
    Gui.drawModalRectWithCustomSizedTexture(
      powerStartPosX - 2,
      powerPosY - 2,
      0F,
      0F,
      widthUsed + 4,
      height + 4,
      widthUsed + 4F,
      height + 4F
    )

    mc.getTextureManager.bindTexture(Power)
    Gui.drawModalRectWithCustomSizedTexture(
      powerFilledPosX,
      powerPosY,
      widthFilled * -1F,
      0F,
      widthFilled,
      height,
      widthUsed,
      height
    )

    var textColor = 0xFFFFFFFF
    var drawText  = true
    if (ConfigHandler.hud.power.hideIfFull) {
      val alpha = (powerBarVisible * 255).toInt
      if (alpha < 4) drawText = false
      textColor = alpha << 24 | 0xFFFFFF
    }

    if (drawText) {
      mc.fontRenderer.drawStringWithShadow("Power: " + power, powerStartPosX, powerPosY - 25F, textColor)
      mc.fontRenderer.drawStringWithShadow("Score: " + score, powerStartPosX, powerPosY - 15F, textColor)
    }
  }

  private def drawLivesAndBombs(res: ScaledResolution, mc: Minecraft, data: IDanmakuCoreData) = {
    val lives = data.getLives
    val bombs = data.getBombs

    starsVisible = fade(
      ConfigHandler.hud.stars.hideIfAboveHigh,
      lives > ConfigHandler.hud.stars.livesHigh && bombs > ConfigHandler.hud.stars.bombsHigh,
      starsVisible
    )
    renderStars(mc, LifeFull, LifeEmpty, lives, 13, res)
    renderStars(mc, BombFull, BombEmpty, bombs, 0, res)
  }

  def fade(config: Boolean, test: Boolean, alpha: Float): Float = {
    if (config) {
      val newAlpha = MathHelper.clamp(alpha + (if (test) -0.01F else 0.1F), 0F, 1F)
      GlStateManager.color(1F, 1F, 1F, newAlpha)
      newAlpha
    } else {
      GlStateManager.color(1F, 1F, 1F, 1F)
      alpha
    }
  }

  private def renderStars(
      mc: Minecraft,
      fullTexture: ResourceLocation,
      emptyTexture: ResourceLocation,
      amount: Int,
      yOffset: Int,
      res: ScaledResolution
  ) = {
    val starX = ConfigHandler.hud.stars.posX
    val starY = res.getScaledHeight - ConfigHandler.hud.stars.posY
    mc.getTextureManager.bindTexture(fullTexture)
    if (amount > 9) {
      Gui.drawModalRectWithCustomSizedTexture(starX, starY - yOffset, 0F, 0F, 8, 8, 8, 8)
      mc.fontRenderer.drawStringWithShadow(amount + "x", starX + 12F, starY - yOffset.toFloat, 0xFFFFFF)
    } else {
      for (i <- 0 until 9) {
        if (i == amount) mc.getTextureManager.bindTexture(emptyTexture)
        Gui.drawModalRectWithCustomSizedTexture(starX + (i * 11), starY - yOffset, 0F, 0F, 8, 8, 8, 8)
      }
    }
  }
}
