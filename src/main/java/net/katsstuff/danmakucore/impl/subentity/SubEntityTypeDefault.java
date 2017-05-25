/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity;

import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.world.World;

public class SubEntityTypeDefault extends SubEntityType {

	public SubEntityTypeDefault(String name) {
		super(name);
	}

	@Override
	public SubEntity instantiate(World world, EntityDanmaku entityDanmaku) {
		return new SubEntityDefault(world, entityDanmaku);
	}

	@SuppressWarnings("WeakerAccess")
	public static class SubEntityDefault extends SubEntityAbstract {

		public SubEntityDefault(World world, EntityDanmaku danmaku) {
			super(world, danmaku);
		}

		@Override
		public void subEntityTick() {
			ShotData shot = danmaku.getShotData();
			int delay = shot.delay();
			if(delay > 0) {
				danmaku.ticksExisted--;
				delay--;

				if(!world.isRemote) {
					if(delay <= 0) {
						danmaku.resetMotion();
					}
					else {
						danmaku.motionX = 0;
						danmaku.motionY = 0;
						danmaku.motionZ = 0;
					}
				}

				shot = shot.setDelay(delay);
				danmaku.setShotData(shot);
			}
			else {
				if(!world.isRemote) {
					RotationData rotationData = danmaku.getRotationData();
					if(rotationData.isEnabled() && danmaku.ticksExisted < rotationData.getEndTime()) {
						rotate();
					}

					double currentSpeed = danmaku.getCurrentSpeed();
					danmaku.accelerate(currentSpeed);

					updateMotionWithGravity();
					hitCheck(entity -> entity != danmaku.getUser().orElse(null) && entity != danmaku.getSource().orElse(null));
				}

				if(danmaku.motionX != 0D && danmaku.motionY != 0D && danmaku.motionZ != 0D) {
					//Projectile helper is buggy. We use this instead
					Vector3 motion = new Vector3(danmaku.motionX, danmaku.motionY, danmaku.motionZ).normalize();

					danmaku.prevRotationPitch = danmaku.rotationPitch;
					danmaku.prevRotationYaw = danmaku.rotationYaw;
					danmaku.rotationPitch = (float)motion.pitch();
					danmaku.rotationYaw = (float)motion.yaw();
				}

				if(danmaku.isInWater()) {
					waterMovement();
				}
			}
		}
	}
}
