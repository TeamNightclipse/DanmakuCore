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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FormStar extends FormGeneric {

	public FormStar() {
		super(LibFormName.STAR);
	}

	@SuppressWarnings("Convert2Lambda")
	@Override
	@SideOnly(Side.CLIENT)
	protected IRenderForm createRenderer() {
		return new IRenderForm() {

			private final float[][] points = {
					{1F, 1F, 1F},
					{-1F, -1F, 1F},
					{-1F, 1F, -1F},
					{1F, -1F, -1}};

			private final int[] tetraIndicies = {0, 1, 2, 3, 0, 1};

			@Override
			@SideOnly(Side.CLIENT)
			public void renderForm(EntityDanmaku danmaku, double x, double y, double z, float entityYaw, float partialTicks,
					RenderManager rendermanager) {
				Tessellator tes = Tessellator.getInstance();
				VertexBuffer buf = tes.getBuffer();
				ShotData shotData = danmaku.getShotData();
				float sizeX = shotData.getSizeX();
				float sizeY = shotData.getSizeY();
				float sizeZ = shotData.getSizeZ();
				int color = shotData.getColor();
				float pitch = danmaku.rotationPitch;
				float yaw = danmaku.rotationYaw;
				float roll = danmaku.getRoll();

				GlStateManager.rotate((danmaku.ticksExisted + partialTicks) * 5F, 1F, 1F, 1F);

				GlStateManager.scale(sizeX, sizeY, sizeZ);
				GlStateManager.rotate(-yaw - 180, 0F, 1F, 0F);
				GlStateManager.rotate(pitch, 1F, 0F, 0F);
				GlStateManager.rotate(roll, 0F, 0F, 1F);

				float red = 1F;
				float green = 1F;
				float blue = 1F;
				float alpha = 1F;

				renderTetrahedron(tes, buf, red, green, blue, alpha);
				GlStateManager.rotate(90F, 1F, 0F, 0F);
				renderTetrahedron(tes, buf, red, green, blue, alpha);
				GlStateManager.rotate(-90F, 1F, 0F, 0F);

				red = (color >> 16 & 255) / 255.0F;
				green = (color >> 8 & 255) / 255.0F;
				blue = (color & 255) / 255.0F;
				alpha = 0.3F;

				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
				GlStateManager.depthMask(false);
				GlStateManager.scale(1.2F, 1.2F, 1.2F);

				renderTetrahedron(tes, buf, red, green, blue, alpha);
				GlStateManager.rotate(90F, 1F, 0F, 0F);
				renderTetrahedron(tes, buf, red, green, blue, alpha);

				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
			}

			@SideOnly(Side.CLIENT)
			private void renderTetrahedron(Tessellator tes, VertexBuffer buf, float r, float g, float b, float a) {
				buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
				for(int i = 0; i < 6; i++) {
					buf.pos(points[tetraIndicies[i]][0], points[tetraIndicies[i]][1], points[tetraIndicies[i]][2]).color(r, g, b, a).endVertex();
				}

				tes.draw();
			}
		};
	}
}
