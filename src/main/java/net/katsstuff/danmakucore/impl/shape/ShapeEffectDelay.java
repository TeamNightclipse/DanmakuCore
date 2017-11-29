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

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

public class ShapeEffectDelay implements IShape {

	private final IShape shape;
	private final int delay;

	public ShapeEffectDelay(IShape shape, int delay) {
		this.shape = shape;
		this.delay = delay;
	}

	@Override
	public ShapeResult draw(Vector3 pos, Quat orientation, int tick) {
		ShapeResult res = shape.draw(pos, orientation, tick);
		if(tick < delay) return ShapeResult.notDone(res.getSpawnedDanmaku());
		else return res;
	}

	@Override
	public void doEffects(Vector3 pos, Quat orientation, int tick, Set<EntityDanmaku> spawnedThisTick, Set<EntityDanmaku> allSpawned) {
		if(tick >= delay) {
			shape.doEffects(pos, orientation, tick - delay, spawnedThisTick, allSpawned);
		}
	}
}
