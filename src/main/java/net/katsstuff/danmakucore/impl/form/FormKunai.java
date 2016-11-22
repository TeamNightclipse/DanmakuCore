/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form;

import org.lwjgl.opengl.GL11;

import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FormKunai extends FormGeneric {

	private final ResourceLocation texture = new ResourceLocation(LibMod.MODID, "textures/entity/danmaku/kunai.png");

	public FormKunai() {
		super(LibFormName.KUNAI);
	}

	@Override
	public ResourceLocation getTexture(EntityDanmaku danmaku) {
		return texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForm(EntityDanmaku danmaku, double x, double y, double z, float entityYaw, float partialTicks, RenderManager rendermanager) {
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		ShotData shotData = danmaku.getShotData();
		float sizeX = shotData.getSizeX();
		float sizeY = shotData.getSizeY();
		float sizeZ = shotData.getSizeZ();
		int color = shotData.getColor();
		float pitch = danmaku.rotationPitch;
		float yaw = danmaku.rotationYaw;
		float roll = danmaku.getRoll();

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		float alpha = 1.0F;

		float u1 = 0F;
		float u2 = 1F;
		float v1 = 0F;
		float v2 = 1F;

		double width = 1.0D;
		double length = 2.0D;

		GL11.glScalef(sizeX, sizeY, sizeZ);
		GL11.glRotatef(-yaw - 180, 0F, 1F, 0F); //TODO: Fix this in a better way?
		GL11.glRotatef(pitch, 1F, 0F, 0F);
		GL11.glRotatef(roll, 0F, 0F, 1F);

		GlStateManager.disableCull();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(width, 0.0F, length).tex(u2, v1).endVertex();
		vb.pos(-width, 0.0F, length).tex(u1, v1).endVertex();
		vb.pos(-width, 0.0F, -length).tex(u1, v2).endVertex();
		vb.pos(width, 0.0F, -length).tex(u2, v2).endVertex();
		tes.draw();

		//What we really want here is to use the luminance as the saturation, and set the luminance to 1 for the texture
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.pos(width, 0D, length).tex(u2, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, length).tex(u1, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, -length).tex(u1, v2).color(red, green, blue, alpha).endVertex();
		vb.pos(width, 0D, -length).tex(u2, v2).color(red, green, blue, alpha).endVertex();
		tes.draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.pos(width, 0D, length).tex(u2, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, length).tex(u1, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, -length).tex(u1, v2).color(red, green, blue, alpha).endVertex();
		vb.pos(width, 0D, -length).tex(u2, v2).color(red, green, blue, alpha).endVertex();
		tes.draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.pos(width, 0D, length).tex(u2, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, length).tex(u1, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, -length).tex(u1, v2).color(red, green, blue, alpha).endVertex();
		vb.pos(width, 0D, -length).tex(u2, v2).color(red, green, blue, alpha).endVertex();
		tes.draw();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.pos(width, 0D, length).tex(u2, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, length).tex(u1, v1).color(red, green, blue, alpha).endVertex();
		vb.pos(-width, 0D, -length).tex(u1, v2).color(red, green, blue, alpha).endVertex();
		vb.pos(width, 0D, -length).tex(u2, v2).color(red, green, blue, alpha).endVertex();
		tes.draw();

		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}
}
