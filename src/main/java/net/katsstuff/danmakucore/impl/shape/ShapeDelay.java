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
	public ShapeResult drawForTick(Vector3 pos, Quat orientation, int tick) {
		if(tick >= delay) {
			return shape.drawForTick(pos, orientation, tick - delay);
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
