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

import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderSpellcard extends Render<EntitySpellcard> {

	public RenderSpellcard(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySpellcard entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GL11.glPushMatrix();
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		float upperV = 0F;
		float upperU = 1F;
		float lowerV = 1F;
		float lowerU = 0F;
		float size = 0.5F;
		bindEntityTexture(entity);
		GL11.glTranslated(x, y, z);
		GL11.glScalef(size, size, size);
		GL11.glRotatef(entity.ticksExisted * 20F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(30F, 0.0F, 0.0F, 1.0F);
		GlStateManager.disableLighting();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vb.pos(size, size, 0D).tex(upperU, upperV).normal(0F, 1F, 0F).endVertex();
		vb.pos(-size, size, 0D).tex(lowerU, upperV).normal(0F, 1F, 0F).endVertex();
		vb.pos(-size, -size, 0D).tex(lowerU, lowerV).normal(0F, 1F, 0F).endVertex();
		vb.pos(size, -size, 0D).tex(upperU, lowerV).normal(0F, 1F, 0F).endVertex();
		tes.draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vb.pos(-size, size, 0D).tex(upperU, upperV).normal(0F, -1F, 0F).endVertex();
		vb.pos(size, size, 0D).tex(lowerU, upperV).normal(0F, -1F, 0F).endVertex();
		vb.pos(size, -size, 0D).tex(lowerU, lowerV).normal(0F, -1F, 0F).endVertex();
		vb.pos(-size, -size, 0D).tex(upperU, lowerV).normal(0F, -1F, 0F).endVertex();
		tes.draw();

		GlStateManager.enableLighting();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpellcard entity) {
		return DanmakuRegistry.SPELLCARD.getObjectById(entity.getSpellcardId()).getTexture();
	}
}
