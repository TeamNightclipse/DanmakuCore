/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.katsstuff.danmakucore.client.helper.RenderHelper;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class FormPellet implements IRenderForm {

	@Override
	public void renderForm(EntityDanmaku danmaku, double x, double y, double z, float entityYaw, float partialTicks, RenderManager rendermanager) {
		float pitch = danmaku.rotationPitch;
		float yaw = danmaku.rotationYaw;
		float roll = danmaku.getRoll();
		ShotData shotData = danmaku.getShotData();
		float sizeX = shotData.getSizeX();
		float sizeY = shotData.getSizeY();
		float sizeZ = shotData.getSizeZ();
		int color = shotData.getColor();
		float alpha = 0.3F;

		Color colorObj = new Color(color);
		float[] hsb = new float[3];
		Color.RGBtoHSB(colorObj.getRed(), colorObj.getGreen(), colorObj.getBlue(), hsb);
		hsb[1] = 0.15F;
		hsb[2] = 1.0F;

		GL11.glRotatef(-yaw, 0F, 1F, 0F);
		GL11.glRotatef(-pitch, 1F, 0F, 0F);
		GL11.glRotatef(roll, 0F, 0F, 1F);
		GL11.glScalef(sizeX, sizeY, sizeZ);

		RenderHelper.drawSphere(8, 16, Color.getHSBColor(hsb[0], hsb[1], hsb[2]).getRGB(), 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.depthMask(false);
		RenderHelper.drawSphere(8, 16, color, alpha, 1.075F);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
	}
}
