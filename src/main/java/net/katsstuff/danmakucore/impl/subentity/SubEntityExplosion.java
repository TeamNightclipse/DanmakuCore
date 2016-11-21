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

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SubEntityExplosion extends SubEntityType {

	private final float strength;

	public SubEntityExplosion(String name, float strength) {
		super(name);
		this.strength = strength;
	}

	@Override
	public SubEntity instantiate(World world, EntityDanmaku entityDanmaku) {
		return new Explosion(world, entityDanmaku, strength);
	}

	private static class Explosion extends SubEntityTypeDefault.SubEntityDefault {

		private final float strength;

		public Explosion(World world, EntityDanmaku danmaku, float strength) {
			super(world, danmaku);
			this.strength = strength;
		}

		@Override
		protected void impact(RayTraceResult rayTrace) {
			super.impact(rayTrace);
			Entity cause;
			Optional<EntityLivingBase> optUser = danmaku.getUser();
			if(optUser.isPresent()) cause = optUser.get();
			else cause = danmaku.getSource().orElse(null);

			if(!world.isRemote) {
				world.createExplosion(cause, rayTrace.hitVec.xCoord, rayTrace.hitVec.yCoord, rayTrace.hitVec.zCoord, strength, false);
			}
		}
	}
}
