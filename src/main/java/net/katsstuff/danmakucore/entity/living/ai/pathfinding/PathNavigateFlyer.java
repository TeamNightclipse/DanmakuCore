/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai.pathfinding;

import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class PathNavigateFlyer extends PathNavigateGround {

	private final EntityDanmakuMob danmakuMob;

	public PathNavigateFlyer(EntityDanmakuMob entityDanmakuIn, World worldIn) {
		super(entityDanmakuIn, worldIn);
		danmakuMob = entityDanmakuIn;
	}

	/**
	 * If on ground or in air and can fly
	 */
	@Override
	protected boolean canNavigate() {
		return danmakuMob.isFlying() || theEntity.onGround || getCanSwim() && isInLiquid();
	}
}
