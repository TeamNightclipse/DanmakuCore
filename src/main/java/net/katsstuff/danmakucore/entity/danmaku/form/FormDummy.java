/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form;

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.util.ResourceLocation;

public class FormDummy extends Form {

	@Override
	public ResourceLocation getTexture(EntityDanmaku danmaku) {
		return new ResourceLocation(LibMod.MODID, "textures/entity/danmaku/White.png");
	}

	@Override
	public IRenderForm getRenderer(EntityDanmaku danmaku) {
		return (danmaku1, x, y, z, entityYaw, partialTicks, rendermanager) -> {};
	}
}
