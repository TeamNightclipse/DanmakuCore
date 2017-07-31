/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.client.render;

import org.lwjgl.opengl.GL11;

import net.katsstuff.danmakucore.entity.EntityFallingData;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderFallingData extends Render<EntityFallingData> {

	private static final ResourceLocation SCORE_GREEN_LOCATION = new ResourceLocation(LibMod.MODID, "textures/entity/falling/score_green.png");
	private static final ResourceLocation SCORE_BLUE_LOCATION = new ResourceLocation(LibMod.MODID, "textures/entity/falling/point_blue.png");
	private static final ResourceLocation POWER_LOCATION = new ResourceLocation(LibMod.MODID, "textures/entity/falling/power_small.png");
	private static final ResourceLocation BIG_POWER_LOCATION = new ResourceLocation(LibMod.MODID, "textures/entity/falling/power_big.png");
	private static final ResourceLocation LIFE_LOCATION = new ResourceLocation(LibMod.MODID, "textures/entity/falling/life.png");
	private static final ResourceLocation BOMB_LOCATION = new ResourceLocation(LibMod.MODID, "textures/entity/falling/bomb.png");

	public RenderFallingData(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFallingData entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		GlStateManager.pushMatrix();
		bindEntityTexture(entity);
		GlStateManager.translate(x, y, z);

		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * -renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		float upperV = 0F;
		float upperU = 1F;
		float lowerV = 1F;
		float lowerU = 0F;
		float size = 0.35F;

		boolean alpha = entity.getDataType() == EntityFallingData.DataType.SCORE_GREEN;

		if(alpha) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		}

		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vb.pos(size, size, 0D).tex(upperU, upperV).normal(0F, 1F, 0F).endVertex();
		vb.pos(-size, size, 0D).tex(lowerU, upperV).normal(0F, 1F, 0F).endVertex();
		vb.pos(-size, -size, 0D).tex(lowerU, lowerV).normal(0F, 1F, 0F).endVertex();
		vb.pos(size, -size, 0D).tex(upperU, lowerV).normal(0F, 1F, 0F).endVertex();
		tes.draw();

		if(alpha) {
			GlStateManager.disableBlend();
		}

		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFallingData entity) {
		switch(entity.getDataType()) {
			case SCORE_GREEN:
				return SCORE_GREEN_LOCATION;
			case SCORE_BLUE:
				return SCORE_BLUE_LOCATION;
			case POWER:
				return POWER_LOCATION;
			case BIG_POWER:
				return BIG_POWER_LOCATION;
			case LIFE:
				return LIFE_LOCATION;
			case BOMB:
				return BOMB_LOCATION;
			default:
				return null;
		}
	}
}