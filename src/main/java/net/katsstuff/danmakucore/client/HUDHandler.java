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
				int widthFull = 128;
				int width = (int)(widthFull * filled);
				int height = 16;

				int baseX = res.getScaledWidth() - 8;
				int baseY = res.getScaledHeight() - 26;

				int x = baseX - width;
				int y = baseY - (height / 2);


				mc.getTextureManager().bindTexture(barResource);

				GlStateManager.color(1F, 1F, 1F);
				Gui.drawModalRectWithCustomSizedTexture(baseX - 130, y - 2, 0F, 0F, widthFull + 4, height + 4, 8, 8);

				GlStateManager.color(0F, 0F, 0F);
				Gui.drawModalRectWithCustomSizedTexture(baseX - 128, y, 0F, 0F, widthFull, height, 8, 8);

				GlStateManager.color(1F, 0F, 0F);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 0F, 0F, width, height, 8, 8);

				for(int i = 0; i < data.getLives(); i++) {
					Gui.drawModalRectWithCustomSizedTexture(8 + (i * 12), baseY - 13, 0F, 0F, 8, 8, 8, 8);
				}

				GlStateManager.color(0F, 1F, 0F);
				for(int i = 0; i < data.getBombs(); i++) {
					Gui.drawModalRectWithCustomSizedTexture(8 + (i * 12), baseY, 0F, 0F, 8, 8, 8, 8);
				}

				mc.fontRendererObj.drawStringWithShadow("Power: " + data.getPower(), baseX - 128, baseY - 30, 0xFFFFFF);
				mc.fontRendererObj.drawStringWithShadow("Score: " + data.getScore(), baseX - 128, baseY - 20, 0xFFFFFF);

				GlStateManager.popMatrix();
			}
		}
	}
}
