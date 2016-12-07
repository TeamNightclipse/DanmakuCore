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

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

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
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		if(!danmaku.world.isRemote) {
			double yaw = angle.yaw();
			double pitch = angle.pitch();
			Vector3 radiusVec = Vector3.fromSpherical(yaw, pitch + radius);
			Vector3 radiusRotateVec = Vector3.fromSpherical(yaw, pitch + radius + 90F);
			Vector3 rotateVec = Vector3.fromSpherical(yaw, pitch + 90F);

			double rotationAngle = Math.toRadians(baseAngle);
			float stepSize = 360F / amount;

			for(int i = 0; i < amount; i++) {
				Vector3 angleVec = angle.rotate(rotationAngle, radiusVec);
				Vector3 distanceVec = angle.rotate(rotationAngle, rotateVec);
				Vector3 rotationVec = angle.rotate(rotationAngle, radiusRotateVec);

				danmaku.pos = pos.offset(distanceVec, distance);
				danmaku.angle = angleVec;
				danmaku.roll = (float)rotationAngle;
				danmaku.rotation = danmaku.rotation.setRotationVec(rotationVec);

				EntityDanmaku spawned = danmaku.asEntity();
				danmaku.world.spawnEntityInWorld(spawned);
				set.add(spawned);
				rotationAngle += stepSize;
			}
		}
		return new Tuple<>(true, set);
	}
}
