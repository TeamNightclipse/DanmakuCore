/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpellcardHandler {

	private Queue<ITextComponent> spellcards = new LinkedList<>();
	private float position = 0F;
	private float prevPosition = 0F;

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		float destinationHeight = 0.02F;

		if(!spellcards.isEmpty() && position > destinationHeight && event.phase == TickEvent.Phase.START) {
			prevPosition = position;
			position = (float)(position - 0.35F * getAcceleration());

			if(position < destinationHeight) {
				position = destinationHeight;
				prevPosition = position;
			}
		}
	}

	@SubscribeEvent
	public void renderSpellcard(RenderGameOverlayEvent.Post event) {
		if(!spellcards.isEmpty()) {
			GlStateManager.pushMatrix();

			float renderPosition = (float)renderPosition(event.getPartialTicks());

			ScaledResolution res = event.getResolution();

			ITextComponent spellcard = spellcards.peek();
			Minecraft mc = Minecraft.getMinecraft();
			String formattedText = spellcard.getFormattedText();

			int height = (int)(renderPosition * res.getScaledHeight());
			int stringWidth = mc.fontRendererObj.getStringWidth(formattedText);
			int fontHeight = mc.fontRendererObj.FONT_HEIGHT;
			int red = 0xFFFF0000;

			mc.fontRendererObj.drawStringWithShadow(formattedText, res.getScaledWidth() - stringWidth, height, 0xFFFFFF);
			Gui.drawRect(res.getScaledWidth() - stringWidth, height + fontHeight, res.getScaledWidth() - 2, height + fontHeight + 1, red);
			Gui.drawRect(res.getScaledWidth() - stringWidth + 40, height + 2 + fontHeight, res.getScaledWidth() - 2, height + fontHeight + 3, red);

			GlStateManager.popMatrix();
		}
	}

	private double renderPosition(float partialTicks) {
		return prevPosition + (position - prevPosition) * partialTicks;
	}

	private double getAcceleration() {
		return (position - 1F) * -1 + 0.01;
	}

	public void addSpellcard(ITextComponent name) {
		spellcards.add(name);
		position = 1F;
		prevPosition = 1F;
	}

	public void removeSpellcard(ITextComponent name) {
		spellcards.remove(name);
	}
}
