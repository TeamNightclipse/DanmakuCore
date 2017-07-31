/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form;

import net.katsstuff.danmakucore.client.particle.GlowTexture;
import net.katsstuff.danmakucore.client.particle.ParticleUtil;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.lib.LibFormName;

public class FormFire extends FormSphere {

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
						danmaku.prevPosY + shot.sizeY() / 2 + (danmaku.posY - danmaku.prevPosY) * coeff,
						danmaku.prevPosZ + (danmaku.posZ - danmaku.prevPosZ) * coeff);
				Vector3 motion = new Vector3(0.0125f * (danmaku.getRNG().nextFloat() - 0.5f), 0.0125f * (danmaku.getRNG().nextFloat() - 0.5f),
						0.0125f * (danmaku.getRNG().nextFloat() - 0.5f));

				ParticleUtil.spawnParticleGlow(danmaku.world, pos, motion, r, g, b, size * 15F, 10, GlowTexture.MOTE);
			}
		}
	}
}
