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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SubEntityTeleport extends SubEntityTypeGeneric {

	public SubEntityTeleport(String name) {
		super(name);
	}

	@Override
	public SubEntity instantiate(World world, EntityDanmaku entityDanmaku) {
		return new Teleport(world, entityDanmaku);
	}

	private static class Teleport extends SubEntityTypeDefault.SubEntityDefault {


		public Teleport(World world, EntityDanmaku danmaku) {
			super(world, danmaku);
		}

		@Override
		public void impact(RayTraceResult rayTrace) {
			Optional<EntityLivingBase> optUser = danmaku.getUser();
			if(optUser.isPresent()) {
				optUser.get().setPositionAndUpdate(danmaku.posX, danmaku.posY, danmaku.posZ);
			}
			super.impact(rayTrace);
		}
	}
}
