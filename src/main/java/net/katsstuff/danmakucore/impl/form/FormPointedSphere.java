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

public class FormPointedSphere extends FormGeneric {

	public FormPointedSphere() {
		super(LibFormName.SPHERE_POINTED);
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
				ShotData shot = danmaku.getShotData();
				int color = shot.getColor();
				float sizeX = shot.getSizeX();
				float sizeY = shot.getSizeY();
				float sizeZ = shot.getSizeZ();

				float centerZ1 = sizeZ * 1.2F / 2.0F;
				float centerZ2 = sizeZ / 2.0F;

				GL11.glRotatef(-yaw - 180, 0F, 1F, 0F); //TODO: Fix this in a better way?
				GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(roll, 0F, 0F, 1F);
				GL11.glScalef(sizeX, sizeY, sizeZ);
				GL11.glTranslatef(0, 0, (-sizeZ / 6) * 4);

				createPointedSphere(tes, vb, 0xFFFFFF, 1F, centerZ1 - centerZ2, sizeZ);

				GlStateManager.scale(1.2F, 1.2F, 1.2F);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

				createPointedSphere(tes, vb, color, 0.6F, 0.0F, sizeZ);

				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
			}

			@SideOnly(Side.CLIENT)
			private void createPointedSphere(Tessellator tes, VertexBuffer vb, int color, float alpha, float zPos, float sizeZ) {
				float r = (color >> 16 & 255) / 255.0F;
				float g = (color >> 8 & 255) / 255.0F;
				float b = (color & 255) / 255.0F;
				float width = 1F;

				int resolutionXY = 8;
				int resolutionZ = 8;

				float maxWidth = width;

				float angleZ = 0F;
				float angleSpanZ = (float)(Math.PI * 2.0D / resolutionXY);

				float segmentLengthZ = sizeZ / (resolutionZ - 1);
				float zPosOld = zPos;
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

				for(int j = 0; j < resolutionZ; j++) {
					zPos += segmentLengthZ;
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

						vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
						vb.pos(xPosOld, yPosOld, zPos).color(r, g, b, alpha).endVertex();
						vb.pos(xPos2Old, yPos2Old, zPosOld).color(r, g, b, alpha).endVertex();
						vb.pos(xPos2, yPos2, zPosOld).color(r, g, b, alpha).endVertex();
						vb.pos(xPos, yPos, zPos).color(r, g, b, alpha).endVertex();
						tes.draw();

						xPosOld = xPos;
						yPosOld = yPos;
						xPos2Old = xPos2;
						yPos2Old = yPos2;
						angleZ += angleSpanZ;
					}
					zPosOld = zPos;
					angle += angleSpan;
					widthOld = width;
				}
			}
		};
	}
}
