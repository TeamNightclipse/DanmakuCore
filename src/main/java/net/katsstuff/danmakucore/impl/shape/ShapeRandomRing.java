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
import java.util.Set;

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuBuilder;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

public class ShapeRandomRing implements IShape {

	private final DanmakuBuilder danmaku;
	private final int amount;
	private final float radius;
	private final double distance;
	private final Set<EntityDanmaku> set = new HashSet<>();

	public ShapeRandomRing(DanmakuBuilder danmaku, int amount, float radius, double distance) {
		this.danmaku = danmaku;
		this.amount = amount;
		this.radius = radius;
		this.distance = distance;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		if(!danmaku.world.isRemote) {
			double yaw = angle.yaw();
			double pitch = angle.pitch();
			Vector3 rotationVec = Vector3.fromSpherical(yaw, pitch + 90F);
			Random rand = danmaku.world.rand;

			for(int i = 0; i < amount; i++) {
				double rotationAngle = Math.toRadians(rand.nextFloat() * 360F);
				Vector3 angleVec = angle.rotateRad(rotationAngle, Vector3.fromSpherical(yaw, pitch + rand.nextFloat() * radius));
				Vector3 distanceVec = angle.rotateRad(rotationAngle, rotationVec);

				danmaku.pos = pos.offset(distanceVec, distance);
				danmaku.angle = angleVec;
				danmaku.roll = (float)rotationAngle;
				danmaku.rotation = danmaku.rotation.setRotationVec(angle);

				EntityDanmaku spawned = danmaku.asEntity();
				set.add(spawned);
				danmaku.world.spawnEntityInWorld(spawned);
			}
		}
		return new Tuple<>(true, set);
	}
}