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
import net.katsstuff.danmakucore.lib.LibFormName;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FormScale extends FormGeneric {

	public FormScale() {
		super(LibFormName.SCALE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForm(EntityDanmaku danmaku, double x, double y, double z, float entityYaw, float partialTicks, RenderManager rendermanager) {
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		float pitch = danmaku.rotationPitch;
		float yaw = danmaku.rotationYaw;
		float roll = danmaku.getRoll();
		ShotData shot = danmaku.getShotData();
		int color = shot.getColor();
		float sizeX = shot.getSizeX();
		float sizeY = shot.getSizeY();
		float sizeZ = shot.getSizeZ();

		float length = 2.0F;
		float alpha = 0.3F;

		GL11.glScalef(sizeX, sizeY, sizeZ);
		GL11.glRotatef(-yaw, 0F, 1F, 0F);
		GL11.glRotatef(-pitch, 1F, 0F, 0F);
		GL11.glRotatef(roll, 0F, 0F, 1F);

		GL11.glScalef(0.5F, 0.5F, length * 0.4F);
		GL11.glTranslatef(0F, 0F, -0.8F);
		RenderHelper.drawSphere(0xFFFFFF, 1F);

		GL11.glTranslatef(0F, 0F, 0.8F);
		GL11.glScalef(2F * 1.2F, 2F * 1.2F, length * 1.2F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.depthMask(false);

		createHalfSphere(tes, vb, color, alpha);

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
	}

	@SideOnly(Side.CLIENT)
	private void createHalfSphere(Tessellator tes, VertexBuffer vb, int color, float alpha) {
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		float length = 1F;
		float width = 1F;

		int resolutionXY = 8;
		int resolutionZ = 8;

		float maxWidth = width;

		float angleZ = 0F;
		float angleSpanZ = (float)(Math.PI * 2.0D / resolutionXY);

		float zPosOld = -length;
		float xPos;
		float yPos;
		float xPos2;
		float yPos2;
		float xPosOld;
		float yPosOld;
		float xPos2Old;
		float yPos2Old;
		float angleSpan = (float)(Math.PI / resolutionZ);
		float angle = (float)((-Math.PI / 2.0F) + angleSpan);
		float widthOld = 0.0F;

		resolutionZ = resolutionZ / 2 + 1;

		GlStateManager.disableCull();

		for(int j = 0; j < resolutionZ; j++) {
			float zPos = MathHelper.sin(angle) * length;
			width = MathHelper.cos(angle) * maxWidth;
			xPosOld = MathHelper.cos(angleZ) * width;
			yPosOld = MathHelper.sin(angleZ) * width;
			xPos2Old = MathHelper.cos(angleZ) * widthOld;
			yPos2Old = MathHelper.sin(angleZ) * widthOld;
			angleZ = angleSpanZ;

			for(int i = 0; i <= resolutionXY; i++) {
				xPos = MathHelper.cos(angleZ) * width;
				yPos = MathHelper.sin(angleZ) * width;
				xPos2 = MathHelper.cos(angleZ) * widthOld;
				yPos2 = MathHelper.sin(angleZ) * widthOld;

				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				vb.pos(xPosOld, yPosOld, zPos).tex(0.0F, 0.0F).color(r, g, b, alpha).endVertex();
				vb.pos(xPos2Old, yPos2Old, zPosOld).tex(0.0F, 1.0F).color(r, g, b, alpha).endVertex();
				vb.pos(xPos2, yPos2, zPosOld).tex(1.0F, 1.0F).color(r, g, b, alpha).endVertex();
				vb.pos(xPos, yPos, zPos).tex(1.0F, 0.0F).color(r, g, b, alpha).endVertex();
				tes.draw();

				xPosOld = xPos;
				yPosOld = yPos;
				xPos2Old = xPos2;
				yPos2Old = yPos2;
				angleZ += angleSpanZ;
			}
			alpha -= j * 0.05F;

			zPosOld = zPos;
			angle += angleSpan;
			widthOld = width;
		}

		GlStateManager.enableCull();
	}
}
