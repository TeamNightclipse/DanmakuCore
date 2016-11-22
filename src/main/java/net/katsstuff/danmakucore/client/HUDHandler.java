/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client;

import java.util.Optional;

import net.katsstuff.danmakucore.capability.IDanmakuCoreData;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
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

	@SubscribeEvent
	public void onDraw(RenderGameOverlayEvent.Post event) {
		if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.thePlayer;
			Optional<IDanmakuCoreData> optData = TouhouHelper.getDanmakuCoreData(player);
			if(optData.isPresent()) {
				IDanmakuCoreData data = optData.get();
				ScaledResolution res = event.getResolution();

				GlStateManager.pushMatrix();

				float filled = data.getPower() / 4F;
				int widthFull = 128;
				int widthNotUsed = 32;
				int height = 10;

				int widthUsed = widthFull - widthNotUsed;
				int widthFilled = (int)(widthUsed * filled);

				int baseX = res.getScaledWidth() + 27;
				int baseY = res.getScaledHeight() - 24;

				int x = baseX - widthFilled - widthNotUsed;
				int y = baseY - (height / 2);

				mc.getTextureManager().bindTexture(POWER_BACKGROUND);
				Gui.drawModalRectWithCustomSizedTexture(baseX - widthFull - 2, y - 2, 0F, 0F, widthUsed + 4, height + 4, widthUsed + 4, height + 4);

				mc.getTextureManager().bindTexture(POWER);
				Gui.drawModalRectWithCustomSizedTexture(x, y, widthFilled * -1, 0F, widthFilled, height, widthUsed, 10);

				int lives = data.getLives();
				mc.getTextureManager().bindTexture(LIFE_FULL);
				GlStateManager.color(1F, 1F, 1F);
				for(int i = 0; i < 9; i++) {
					if(i == lives) {
						mc.getTextureManager().bindTexture(LIFE_EMPTY);
					}
					Gui.drawModalRectWithCustomSizedTexture(8 + (i * 11), baseY - 13, 0F, 0F, 8, 8, 8, 8);
				}

				int bombs = data.getBombs();
				mc.getTextureManager().bindTexture(BOMB_FULL);
				for(int i = 0; i < 9; i++) {
					if(i == bombs) {
						mc.getTextureManager().bindTexture(BOMB_EMPTY);
					}
					Gui.drawModalRectWithCustomSizedTexture(8 + (i * 11), baseY, 0F, 0F, 8, 8, 8, 8);
				}

				mc.fontRendererObj.drawStringWithShadow("Power: " + data.getPower(), baseX - widthFull, baseY - 30, 0xFFFFFF);
				mc.fontRendererObj.drawStringWithShadow("Score: " + data.getScore(), baseX - widthFull, baseY - 20, 0xFFFFFF);

				GlStateManager.popMatrix();
			}
		}
	}
}
