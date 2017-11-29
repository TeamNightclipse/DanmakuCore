/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

public class ShapeDelay implements IShape {

	private final IShape shape;
	private final int delay;

	public ShapeDelay(IShape shape, int delay) {
		this.shape = shape;
		this.delay = delay;
	}

	@Override
	public ShapeResult draw(Vector3 pos, Quat orientation, int tick) {
		if(tick >= delay) {
			return shape.draw(pos, orientation, tick - delay);
		}
		else {
			return ShapeResult.done(ImmutableSet.of());
		}
	}

	@Override
	public void doEffects(Vector3 pos, Quat orientation, int tick, Set<EntityDanmaku> spawnedThisTick, Set<EntityDanmaku> allSpawned) {
		if(tick >= delay) {
			shape.doEffects(pos, orientation, tick - delay, spawnedThisTick, allSpawned);
		}
	}
}
