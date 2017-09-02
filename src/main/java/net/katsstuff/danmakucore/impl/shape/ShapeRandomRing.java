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
import java.util.Random;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

public class ShapeRandomRing implements IShape {

	private final DanmakuTemplate danmaku;
	private final int amount;
	private final float radius;
	private final double distance;

	public ShapeRandomRing(DanmakuTemplate danmaku, int amount, float radius, double distance) {
		this.danmaku = danmaku;
		this.amount = amount;
		this.radius = radius;
		this.distance = distance;
	}

	@Override
	public ShapeResult draw(Vector3 pos, Quat orientation, int tick) {
		HashSet<EntityDanmaku> set = new HashSet<>();
		if(!danmaku.world.isRemote) {
			Random rand = danmaku.world.rand;
			Quat rotatedOrientation = orientation.multiply(Quat.fromAxisAngle(Vector3.Right(), 90));

			for(int i = 0; i < amount; i++) {
				Quat rotate = rotatedOrientation.multiply(Quat.fromAxisAngle(Vector3.Up(), rand.nextDouble() * 360D)
						.multiply(Quat.fromAxisAngle(Vector3.Left(), 90D - rand.nextDouble() * radius)));
				danmaku.direction = Vector3.Forward().rotate(rotate);
				danmaku.pos = pos.offset(danmaku.direction, distance);
				EntityDanmaku spawned = this.danmaku.asEntity();
				danmaku.asEntity().world.spawnEntity(spawned);
				set.add(spawned);
			}
		}
		return ShapeResult.done(set);
	}
}