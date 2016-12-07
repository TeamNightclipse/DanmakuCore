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
import net.katsstuff.danmakucore.lib.data.LibItems;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderSpellcard extends Render<EntitySpellcard> {

	public RenderSpellcard(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySpellcard entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GL11.glPushMatrix();

		float size = 1.5F;

		GL11.glTranslated(x, y, z);
		GL11.glScalef(size, size, size);
		GL11.glRotatef(entity.ticksExisted * 20F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(30F, 0.0F, 0.0F, 1.0F);

		Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(LibItems.SPELLCARD, 1, entity.getSpellcardId()),
				ItemCameraTransforms.TransformType.GROUND);

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpellcard entity) {
		return DanmakuRegistry.SPELLCARD.getObjectById(entity.getSpellcardId()).getTexture();
	}
}
