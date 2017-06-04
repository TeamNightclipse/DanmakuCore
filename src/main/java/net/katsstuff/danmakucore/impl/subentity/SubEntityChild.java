/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity;

import java.util.Optional;

import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.handler.PlayerChangeHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SubEntityChild extends SubEntityType {

	public SubEntityChild(String name) {
		super(name);
	}

	@Override
	public SubEntity instantiate(World world, EntityDanmaku entityDanmaku) {
		return new Child(world, entityDanmaku);
	}

	public static class Child extends SubEntityAbstract {

		private Child(World world, EntityDanmaku danmaku) {
			super(world, danmaku);
		}

		@Override
		public void subEntityTick() {
			ShotData shot = danmaku.getShotData();
			int delay = shot.delay();

			if(delay > 0) {
				danmaku.ticksExisted--;
				delay--;

				if(delay == 0 && shot.end() == 1) {
					danmaku.delete();
					return;
				}

				shot = shot.setDelay(delay);
				danmaku.setShotData(shot);
			}

			if(!world.isRemote) {
				RotationData rotationData = danmaku.getRotationData();
				if(rotationData.isEnabled() && danmaku.ticksExisted < rotationData.getEndTime()) {
					rotate();
				}

				Optional<Entity> sourceOpt = danmaku.getSource();
				if(sourceOpt.isPresent() && !sourceOpt.get().isDead) {
					Entity source = sourceOpt.get();

					danmaku.accelerate(danmaku.getCurrentSpeed());

					if(source instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer)source;
						double[] change = PlayerChangeHandler.getPlayerChange(player);
						danmaku.motionX += change[0];
						danmaku.motionY += change[1];
						danmaku.motionZ += change[2];
					}
					else {
						danmaku.motionX += source.motionX;
						danmaku.motionY += source.motionY;
						danmaku.motionZ += source.motionZ;
					}

				}
				else {
					danmaku.setDead();
					return;
				}

				if(delay >= 0) {
					hitCheck(entity -> entity != danmaku.getUser().orElse(null) && entity != sourceOpt.orElse(null));
				}
			}

			if(danmaku.isInWater()) {
				waterMovement();
			}
		}
	}
}
