/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.render;

import net.katsstuff.danmakucore.client.models.ModelTenguTest;
import net.katsstuff.danmakucore.entity.living.boss.EntityTenguTest;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderTenguTest extends RenderLiving<EntityTenguTest> {

	public RenderTenguTest(RenderManager renderManager) {
		super(renderManager, new ModelTenguTest(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTenguTest entity) {
		return new ResourceLocation(LibMod.MODID, "textures/entity/tengu_crow.png");
	}
}
