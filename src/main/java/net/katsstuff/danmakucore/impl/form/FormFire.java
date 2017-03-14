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
import net.katsstuff.danmakucore.client.particle.GlowTexture;
import net.katsstuff.danmakucore.client.particle.ParticleUtil;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FormFire extends FormGeneric {

	public FormFire() {
		super(LibFormName.FIRE);
	}

	@Override
	public void onTick(EntityDanmaku danmaku) {
		if(danmaku.world.isRemote) {
			ShotData shot = danmaku.getShotData();
			int color = shot.color();
			float r = Math.max(0.05F, (color >> 16 & 255) / 255.0F);
			float g = Math.max(0.05F, (color >> 8 & 255) / 255.0F);
			float b = Math.max(0.05F, (color & 255) / 255.0F);
			float size = (shot.sizeX() + shot.sizeY() + shot.sizeZ()) / 3;

			for(double i = 0; i < 15; i++) {
				double coeff = i / 15D;
				Vector3 pos = new Vector3(danmaku.prevPosX + (danmaku.posX - danmaku.prevPosX) * coeff,
						danmaku.prevPosY + (danmaku.posY - danmaku.prevPosY) * coeff, danmaku.prevPosZ + (danmaku.posZ - danmaku.prevPosZ) * coeff);
				Vector3 motion = new Vector3(0.0125f * (danmaku.getRNG().nextFloat() - 0.5f), 0.0125f * (danmaku.getRNG().nextFloat() - 0.5f),
						0.0125f * (danmaku.getRNG().nextFloat() - 0.5f));

				ParticleUtil.spawnParticleGlow(danmaku.world, pos, motion, r, g, b, size * 15F, 10, GlowTexture.MOTE);
			}
		}
	}

	@SuppressWarnings("Convert2Lambda")
	@Override
	@SideOnly(Side.CLIENT)
	protected IRenderForm createRenderer() {
		return new IRenderForm() {

			@Override
			@SideOnly(Side.CLIENT)
			public void renderForm(EntityDanmaku danmaku, double x, double y, double z, float entityYaw, float partialTicks,
					RenderManager renderManager) {
				float pitch = danmaku.rotationPitch;
				float yaw = danmaku.rotationYaw;
				float roll = danmaku.getRoll();
				ShotData shotData = danmaku.getShotData();
				float sizeX = shotData.getSizeX();
				float sizeY = shotData.getSizeY();
				float sizeZ = shotData.getSizeZ();
				int color = shotData.getColor();
				float alpha = 0.3F;

				GL11.glRotatef(-yaw, 0F, 1F, 0F);
				GL11.glRotatef(-pitch, 1F, 0F, 0F);
				GL11.glRotatef(roll, 0F, 0F, 1F);
				GL11.glScalef(sizeX, sizeY, sizeZ);

				RenderHelper.drawSphere(0xFFFFFF, 1F);

				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
				GlStateManager.depthMask(false);
				GlStateManager.scale(1.2F, 1.2F, 1.2F);
				RenderHelper.drawSphere(color, alpha);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
			}
		};
	}
}
