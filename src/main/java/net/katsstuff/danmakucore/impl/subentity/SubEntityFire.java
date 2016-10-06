/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity;

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SubEntityFire extends SubEntityTypeCore {

	private final float multiplier;

	public SubEntityFire(String name, float multiplier) {
		super(name);
		this.multiplier = multiplier;
	}

	@Override
	public SubEntity instantiate(World world, EntityDanmaku entityDanmaku) {
		return new Fire(world, entityDanmaku, multiplier);
	}

	private static class Fire extends SubEntityTypeDefault.SubEntityDefault {

		private final float multiplier;

		public Fire(World world, EntityDanmaku danmaku, float multiplier) {
			super(world, danmaku);
			this.multiplier = multiplier;
		}

		//TODO: Longer?
		@Override
		public void impactEntity(RayTraceResult rayTrace) {
			super.impactEntity(rayTrace);
			rayTrace.entityHit.setFire((int)(danmaku.getShotData().damage() * multiplier));
		}
	}
}
