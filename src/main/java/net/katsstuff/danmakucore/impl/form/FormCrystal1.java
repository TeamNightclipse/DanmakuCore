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
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A two sided normal crystal. Pointy on both sides.
 */
public class FormCrystal1 extends FormGeneric {

	public FormCrystal1() {
		super(LibFormName.CRYSTAL1);
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
				float pitch = danmaku.rotationPitch;
				float yaw = danmaku.rotationYaw;
				float roll = danmaku.getRoll();
				ShotData shotData = danmaku.getShotData();
				float sizeX = shotData.getSizeX();
				float sizeY = shotData.getSizeY();
				float sizeZ = shotData.getSizeZ();
				int color = shotData.getColor();

				GL11.glRotatef(-yaw - 180F, 0F, 1F, 0F);
				GL11.glRotatef(pitch - 90F, 1F, 0F, 0F);
				GL11.glRotatef(roll, 0F, 0F, 1F);
				GL11.glScalef(sizeX, sizeY, sizeZ);

				createShapeOneEnd(tes, vb, 0xFFFFFF, 1F, 0.5F, 1.25F);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
				GlStateManager.depthMask(false);
				createShapeOneEnd(tes, vb, color, 0.3F, 0.6F, 1.25F * 1.2F);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
			}

			@SideOnly(Side.CLIENT)
			private void createShapeOneEnd(Tessellator tes, VertexBuffer vb, int color, float alpha, float radius, float pointy) {
				float r = (color >> 16 & 255) / 255.0F;
				float g = (color >> 8 & 255) / 255.0F;
				float b = (color & 255) / 255.0F;
				float corner = (MathHelper.cos((float)Math.toRadians(45)) * radius);

				vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				vb.pos(0F, -pointy, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(0F, -radius, radius).color(r, g, b, alpha).endVertex();
				vb.pos(corner, -radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(radius, -radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(corner, -radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(0F, -radius, -radius).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, -radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(-radius, -radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, -radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(0F, -radius, radius).color(r, g, b, alpha).endVertex();
				tes.draw();

				vb.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
				vb.pos(0F, -radius, radius).color(r, g, b, alpha).endVertex();
				vb.pos(0F, radius, radius).color(r, g, b, alpha).endVertex();
				vb.pos(corner, -radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(corner, radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(radius, -radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(radius, radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(corner, -radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(corner, radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(0F, -radius, -radius).color(r, g, b, alpha).endVertex();
				vb.pos(0F, radius, -radius).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, -radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(-radius, -radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(-radius, radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, -radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(0F, -radius, radius).color(r, g, b, alpha).endVertex();
				vb.pos(0F, radius, radius).color(r, g, b, alpha).endVertex();
				tes.draw();

				vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				vb.pos(0F, pointy, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(0F, radius, radius).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(-radius, radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(-corner, radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(0F, radius, -radius).color(r, g, b, alpha).endVertex();
				vb.pos(corner, radius, -corner).color(r, g, b, alpha).endVertex();
				vb.pos(radius, radius, 0F).color(r, g, b, alpha).endVertex();
				vb.pos(corner, radius, corner).color(r, g, b, alpha).endVertex();
				vb.pos(0F, radius, radius).color(r, g, b, alpha).endVertex();
				tes.draw();
			}
		};
	}
}
