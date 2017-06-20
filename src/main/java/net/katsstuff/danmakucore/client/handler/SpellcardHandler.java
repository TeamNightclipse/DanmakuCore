/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.katsstuff.danmakucore.network.SpellcardInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpellcardHandler {

	private Map<UUID, SpellcardInfoClient> spellcards = new HashMap<>();

	@SubscribeEvent
	public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		spellcards.clear();
	}

	@SubscribeEvent
	public void renderSpellcard(RenderGameOverlayEvent.Post event) {
		int yOffset = 0;

		Minecraft mc = Minecraft.getMinecraft();
		float partial = event.getPartialTicks();
		ScaledResolution res = event.getResolution();

		int red = 0xFFFF0000;
		int fontHeight = mc.fontRenderer.FONT_HEIGHT;

		GlStateManager.pushMatrix();
		for(SpellcardInfoClient info : spellcards.values()) {
			int renderX = (int)info.getRenderPosX(res, partial);
			int renderY = (int)(info.getRenderPosY(res, partial) + yOffset);

			String formattedText = info.getName().getFormattedText();
			int stringWidth = mc.fontRenderer.getStringWidth(formattedText);

			if(info.shouldMirrorText()) {
				mc.fontRenderer.drawStringWithShadow(formattedText, renderX - stringWidth, renderY, 0xFFFFFF);
				Gui.drawRect(renderX - stringWidth, renderY + fontHeight, renderX - 2, renderY + fontHeight + 1, red);
				Gui.drawRect(renderX - stringWidth + 40, renderY + 2 + fontHeight, renderX - 2, renderY + fontHeight + 3, red);
			}
			else {
				mc.fontRenderer.drawStringWithShadow(formattedText, renderX, renderY, 0xFFFFFF);
				Gui.drawRect(renderX, renderY + fontHeight, renderX + stringWidth - 2, renderY + fontHeight + 1, red);
				Gui.drawRect(renderX, renderY + 2 + fontHeight, renderX + stringWidth - 42, renderY + fontHeight + 3, red);
			}

			yOffset += 16;
		}

		GlStateManager.popMatrix();
	}

	public void handlePacket(SpellcardInfoPacket.Message message) {
		SpellcardInfoPacket.Action action = message.getAction();

		switch(action) {
			case ADD:
				spellcards.put(message.getUuid(), new SpellcardInfoClient(message));
				break;
			case REMOVE:
				spellcards.remove(message.getUuid());
				break;
			default:
				SpellcardInfoClient infoClient = spellcards.get(message.getUuid());
				if(infoClient != null) {
					infoClient.handlePacket(message);
				}
				break;
		}
	}
}
