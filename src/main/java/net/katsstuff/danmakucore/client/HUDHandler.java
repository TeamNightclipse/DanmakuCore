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

public class HUDHandler {

	private final ResourceLocation barResource = new ResourceLocation(LibMod.MODID, "textures/entity/danmaku/White.png");

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
				int width = (int)(128 * filled);
				int height = 16;

				int baseX = res.getScaledWidth() - 24;
				int baseY = res.getScaledHeight() - 16;

				int x = baseX - width;
				int y = baseY - (height / 2);


				mc.getTextureManager().bindTexture(barResource);
				GlStateManager.color(1F, 0F, 0F);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 0F, 0F, width, height, 8, 8);
				mc.fontRendererObj.drawStringWithShadow("Power: " + data.getPower(), baseX - 64, baseY - 30, 0xFFFFFF);
				mc.fontRendererObj.drawStringWithShadow("Danmaku score: " + data.getScore(), baseX - 108, baseY - 18, 0xFFFFFF);

				GlStateManager.popMatrix();
			}
		}
	}
}
