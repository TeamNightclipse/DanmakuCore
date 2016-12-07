/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.handler;

import java.util.ArrayList;
import java.util.List;

import net.katsstuff.danmakucore.entity.living.boss.EntityDanmakuBoss;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfoLerping;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BossBarHandler {

	private static final ResourceLocation STAR_LOCATION = new ResourceLocation(LibMod.MODID, "textures/gui/boss/star.png");
	public final List<EntityDanmakuBoss> danmakuBosses = new ArrayList<>();

	@SubscribeEvent
	public void onBossBar(RenderGameOverlayEvent.BossInfo event) {
		BossInfoLerping bossInfo = event.getBossInfo();
		Minecraft mc = Minecraft.getMinecraft();

		//ConcurrentModificationException
		//noinspection ForLoopReplaceableByForEach
		for(int i = 0; i < danmakuBosses.size(); i++) {
			EntityDanmakuBoss boss = danmakuBosses.get(i);
			if(bossInfo.getUniqueId().equals(boss.getBossInfoUUID())) {

				GlStateManager.pushMatrix();

				int baseX = 5;
				int baseY = event.getY() - 7;

				int remainingSpellcards = boss.remainingSpellcards();
				if(remainingSpellcards > 0) {
					mc.getTextureManager().bindTexture(STAR_LOCATION);

					if(remainingSpellcards > 8) {
						Gui.drawModalRectWithCustomSizedTexture(baseX, baseY, 0F, 0F, 12, 12, 12F, 12F);
						mc.fontRendererObj.drawStringWithShadow("x" + remainingSpellcards, baseX + 14, baseY + 4, 0xFFFFFF);
					}
					else {
						for(int j = 0; j < remainingSpellcards; j++) {
							Gui.drawModalRectWithCustomSizedTexture(baseX + (j * 14), baseY, 0F, 0F, 12, 12, 12F, 12F);
						}
					}
				}

				if(ConfigHandler.entities.circleBossBar) {
					event.setCanceled(true);

					//TODO
				}

				GlStateManager.popMatrix();
			}
		}
	}
}
