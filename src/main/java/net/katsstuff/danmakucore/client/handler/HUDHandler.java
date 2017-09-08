/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler;

import java.util.Optional;

import net.katsstuff.danmakucore.capability.IDanmakuCoreData;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.helper.MathUtil;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HUDHandler {

	private static final ResourceLocation LIFE_FULL = new ResourceLocation(LibMod.MODID, "textures/gui/hud/life_full.png");
	private static final ResourceLocation LIFE_EMPTY = new ResourceLocation(LibMod.MODID, "textures/gui/hud/life_empty.png");
	private static final ResourceLocation BOMB_FULL = new ResourceLocation(LibMod.MODID, "textures/gui/hud/bomb_full.png");
	private static final ResourceLocation BOMB_EMPTY = new ResourceLocation(LibMod.MODID, "textures/gui/hud/bomb_empty.png");
	private static final ResourceLocation POWER = new ResourceLocation(LibMod.MODID, "textures/gui/hud/power.png");
	private static final ResourceLocation POWER_BACKGROUND = new ResourceLocation(LibMod.MODID, "textures/gui/hud/power_background.png");

	private float powerBarVisible = 1F;
	private float starsVisible = 1F;

	@SubscribeEvent
	public void onDraw(RenderGameOverlayEvent.Post event) {
		if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			Optional<IDanmakuCoreData> optData = TouhouHelper.getDanmakuCoreData(player);
			if(optData.isPresent()) {
				IDanmakuCoreData data = optData.get();
				ScaledResolution res = event.getResolution();

				GlStateManager.pushMatrix();

				drawPower(res, mc, data.getPower(), data.getScore());
				drawLivesAndBombs(res, mc, data);

				GlStateManager.popMatrix();
			}
		}
	}

	private void drawPower(ScaledResolution res, Minecraft mc, float power, int score) {
		float filled = power > 4F ? 1F : power / 4F;
		int widthEnd = ConfigHandler.hud.power.widthEnd;
		int widthStart = ConfigHandler.hud.power.widthStart;

		int height = 10;

		int widthUsed = widthEnd - widthStart;
		int widthFilled = (int)(widthUsed * filled);

		int powerPosX = res.getScaledWidth() + ConfigHandler.hud.power.posX;
		int powerPosY = res.getScaledHeight() - ConfigHandler.hud.power.posY;

		int powerStartPosX = powerPosX - widthEnd;
		int powerFilledPosX = powerPosX - widthFilled - widthStart;

		if(ConfigHandler.hud.power.hideIfFull) {
			if(MathUtil.fuzzyEqual(filled, 1F)) {
				powerBarVisible -= 0.01F;
			}
			else {
				powerBarVisible += 0.1F;
			}
			powerBarVisible = MathHelper.clamp(powerBarVisible, 0F, 1F);

			GlStateManager.color(1F, 1F, 1F, powerBarVisible);
		}
		else {
			GlStateManager.color(1F, 1F, 1F, 1F);
		}

		mc.getTextureManager().bindTexture(POWER_BACKGROUND);
		Gui.drawModalRectWithCustomSizedTexture(powerStartPosX - 2, powerPosY - 2, 0F, 0F, widthUsed + 4, height + 4, widthUsed + 4F,
				height + 4F);

		mc.getTextureManager().bindTexture(POWER);
		Gui.drawModalRectWithCustomSizedTexture(powerFilledPosX, powerPosY, widthFilled * -1F, 0F, widthFilled, height, widthUsed,
				height);

		int textColor = 0xFFFFFFFF;
		boolean drawText = true;
		if(ConfigHandler.hud.power.hideIfFull) {
			int alpha = (int)(powerBarVisible * 255);
			if(alpha < 4) {
				drawText = false;
			}

			//noinspection NumericOverflow
			textColor = alpha << 24 | 0xFFFFFF;
		}

		if(drawText) {
			mc.fontRenderer.drawStringWithShadow("Power: " + power, powerStartPosX, powerPosY - 25F, textColor);
			mc.fontRenderer.drawStringWithShadow("Score: " + score, powerStartPosX, powerPosY - 15F, textColor);
		}
	}

	private void drawLivesAndBombs(ScaledResolution res, Minecraft mc, IDanmakuCoreData data) {
		int lives = data.getLives();
		int bombs = data.getBombs();

		fadeStars(lives, bombs);
		renderStars(mc, LIFE_FULL, LIFE_EMPTY, lives, 13, res);
		renderStars(mc, BOMB_FULL, BOMB_EMPTY, bombs, 0, res);
	}

	private void fadeStars(int lives, int bombs) {
		if(ConfigHandler.hud.stars.hideIfAboveHigh) {
			if(lives > ConfigHandler.hud.stars.livesHigh && bombs > ConfigHandler.hud.stars.bombsHigh) {
				starsVisible -= 0.01F;
			}
			else {
				starsVisible += 0.1F;
			}
			starsVisible = MathHelper.clamp(starsVisible, 0F, 1F);

			GlStateManager.color(1F, 1F, 1F, starsVisible);
		}
		else {
			GlStateManager.color(1F, 1F, 1F, 1F);
		}
	}

	private static void renderStars(Minecraft mc, ResourceLocation fullTexture, ResourceLocation emptyTexture, int amount, int yOffset, ScaledResolution res) {
		int starX = ConfigHandler.hud.stars.posX;
		int starY = res.getScaledHeight() - ConfigHandler.hud.stars.posY;

		mc.getTextureManager().bindTexture(fullTexture);

		if(amount > 9) {
			Gui.drawModalRectWithCustomSizedTexture(starX, starY - yOffset, 0F, 0F, 8, 8, 8, 8);
			mc.fontRenderer.drawStringWithShadow(amount + "x", starX + 12F, starY - (float)yOffset, 0xFFFFFF);
		}
		else {
			for(int i = 0; i < 9; i++) {
				if(i == amount) {
					mc.getTextureManager().bindTexture(emptyTexture);
				}
				Gui.drawModalRectWithCustomSizedTexture(starX + (i * 11), starY - yOffset, 0F, 0F, 8, 8, 8, 8);
			}
		}
	}
}
