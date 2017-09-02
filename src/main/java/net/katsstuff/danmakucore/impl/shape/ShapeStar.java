/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape;

import java.util.HashSet;

import com.google.common.collect.ImmutableSet;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

public class ShapeStar implements IShape {

	private static final int[][] WAYS = {{0},

			{1}, {1, 1}, {1, 0, 2, 0}, {1, 0, 3, 0}, {1, 3, 1}, {1, 4, 1}, {1, 5, 1}, {1, 3, 3, 1}, {1, 5, 3, 0}, {1, 4, 4, 1},

			{1, 6, 4, 0}, {1, 5, 5, 1}, {1, 3, 5, 3, 1}, {1, 3, 6, 3, 1}, {1, 4, 5, 4, 1}, {1, 4, 6, 4, 1}, {1, 4, 7, 4, 1}, {1, 3, 5, 5, 3, 1},
			{1, 3, 6, 6, 3}, {1, 3, 6, 6, 3, 1},

			{1, 5, 9, 5, 1}, {1, 3, 7, 7, 3, 1}, {1, 6, 9, 6, 1}, {1, 4, 7, 7, 4, 1}, {1, 3, 5, 7, 5, 3, 1}, {1, 3, 5, 8, 5, 3, 1},
			{1, 3, 5, 9, 5, 3, 1}, {1, 3, 6, 8, 6, 3, 1}, {1, 3, 6, 9, 6, 3, 1}, {1, 4, 6, 8, 6, 4, 1},

			{1, 3, 6, 11, 6, 3, 1}, {1, 3, 6, 12, 6, 3, 1}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0},

			{0}, {0}, {0}, {1, 3, 6, 12, 12, 6, 3, 1}

	};

	private final DanmakuTemplate danmaku;
	private final int amount;
	private final float angleZ;
	private final float baseAngle;
	private final double distance;

	public ShapeStar(DanmakuTemplate danmaku, int amount, float angleZ, float baseAngle, double distance) {
		this.danmaku = danmaku;
		this.amount = amount;
		this.angleZ = angleZ;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public ShapeResult drawForTick(Vector3 pos, Quat orientation, int tick) {
		if(amount >= WAYS.length) return ShapeResult.done(ImmutableSet.of());
		HashSet<EntityDanmaku> set = new HashSet<>();
		if(!danmaku.world.isRemote) {
			Vector3 localForward = Vector3.Forward().rotate(orientation);
			Vector3 localBackward = Vector3.Backward().rotate(orientation);

			Vector3 frontPos = pos.offset(localForward, distance);
			Vector3 backPos = pos.offset(localBackward, distance);

			float angleBase = 0F;
			float angleSpan = 0F;
			if(WAYS[amount].length >= 2) {
				angleSpan = 360F / (WAYS[amount].length - 1);
			}
			boolean flagFB = false;
			float slope = 0F;

			danmaku.roll = angleZ;


			for(int i = 0; i < WAYS[amount].length; i++) {
				if(WAYS[amount][i] == 1) {
					if(!flagFB) {
						danmaku.pos = frontPos;
						danmaku.direction = localForward;
						EntityDanmaku spawned = danmaku.asEntity();
						set.add(spawned);
						danmaku.world.spawnEntity(spawned);
						flagFB = true;
					}
					else {
						danmaku.pos = backPos;
						danmaku.direction = localBackward;
						EntityDanmaku spawned = danmaku.asEntity();
						set.add(spawned);
						danmaku.world.spawnEntity(spawned);
					}
				}
				else {
					danmaku.pos = pos;
					danmaku.direction = localForward;
					ShapeRing shape = new ShapeRing(danmaku, WAYS[amount][i], angleBase, baseAngle + slope, distance);
					set.addAll(shape.drawForTick(pos, orientation, 0).getSpawnedDanmaku());
					slope += 180F / WAYS[amount].length;
				}

				angleBase += angleSpan;
			}
		}
		return ShapeResult.done(set);
	}
}
