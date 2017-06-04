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
import java.util.Set;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;

public class ShapeRing implements IShape {

	private final DanmakuTemplate danmaku;
	private final int amount;
	private final float radius;
	private final float baseAngle;
	private final double distance;
	private final Set<EntityDanmaku> set = new HashSet<>();

	public ShapeRing(DanmakuTemplate danmaku, int amount, float radius, float baseAngle, double distance) {
		this.danmaku = danmaku;
		this.amount = amount;
		this.radius = radius;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Quat orientation, int tick) {
		if(!danmaku.world.isRemote) {

			float wideAngle = 360F;
			double rotateAngle = -wideAngle / 2D;
			double stepSize = wideAngle / (amount - 1);
			rotateAngle += baseAngle;

			Quat rotatedOrientation = orientation.multiply(Quat.fromAxisAngle(Vector3.Right(), 90));

			for(int i = 0; i < amount; i++) {
				Quat rotate = rotatedOrientation.multiply(
						Quat.fromAxisAngle(Vector3.Up(), rotateAngle).multiply(Quat.fromAxisAngle(Vector3.Left(), 90D - radius)));
				danmaku.direction = Vector3.Forward().rotate(rotate);
				danmaku.pos = pos.offset(danmaku.direction, distance);
				danmaku.roll = (float)(orientation.pitch() * MathHelper.sin((float)rotateAngle));
				EntityDanmaku spawned = this.danmaku.asEntity();
				danmaku.asEntity().world.spawnEntityInWorld(spawned);
				set.add(spawned);
				rotateAngle += stepSize;
			}
		}
		return new Tuple<>(true, set);
	}
}
