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

import net.katsstuff.danmakucore.client.helper.RenderHelper;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm;
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

	@SuppressWarnings("Convert2Lambda")
	@Override
	@SideOnly(Side.CLIENT)
	protected IRenderForm createRenderer() {
		return new IRenderForm() {

			@Override
			@SideOnly(Side.CLIENT)
			public void renderForm(EntityDanmaku danmaku, double x, double y, double z, float entityYaw, float partialTicks,
					RenderManager rendermanager) {
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer vb = tes.getBuffer();
				ShotData shotData = danmaku.getShotData();
				int color = shotData.getColor();

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

				RenderHelper.transformEntity(danmaku);

				GlStateManager.disableCull();
				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				vb.pos(width, 0.0F, length).tex(u2, v1).endVertex();
				vb.pos(-width, 0.0F, length).tex(u1, v1).endVertex();
				vb.pos(-width, 0.0F, -length).tex(u1, v2).endVertex();
				vb.pos(width, 0.0F, -length).tex(u2, v2).endVertex();
				tes.draw();

				//What we really want here is to use the luminance as the saturation, and set the luminance to 1 for the texture
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
				for(int i = 0; i < 4; i++) {
					vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
					vb.pos(width, 0D, length).tex(u2, v1).color(red, green, blue, alpha).endVertex();
					vb.pos(-width, 0D, length).tex(u1, v1).color(red, green, blue, alpha).endVertex();
					vb.pos(-width, 0D, -length).tex(u1, v2).color(red, green, blue, alpha).endVertex();
					vb.pos(width, 0D, -length).tex(u2, v2).color(red, green, blue, alpha).endVertex();
					tes.draw();
				}

				GlStateManager.disableBlend();
				GlStateManager.enableCull();
			}
		};
	}
}
