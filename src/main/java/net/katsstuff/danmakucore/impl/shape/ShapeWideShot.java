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
import net.minecraft.util.math.MathHelper;

public class ShapeWideShot implements IShape {

	private final DanmakuTemplate danmaku;
	private final int amount;
	private final float wideAngle;
	private final float baseAngle;
	private final double distance;
	private final Set<EntityDanmaku> set = new HashSet<>();

	public ShapeWideShot(DanmakuTemplate danmaku, int amount, float wideAngle, float baseAngle, double distance) {
		this.danmaku = danmaku;
		this.amount = amount;
		this.wideAngle = wideAngle;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		if(!danmaku.world.isRemote) {
			Vector3 rotateVec = Vector3.fromSpherical(angle.yaw(), angle.pitch() + 90); //TODO: How do do this without relying on yaw and pitch?

			double rotateAngle = Math.toRadians(-wideAngle / 2F);
			double stepSize = Math.toRadians(wideAngle / (amount - 1));
			rotateAngle += Math.toRadians(baseAngle);

			for(int i = 0; i < amount; i++) {
				Vector3 angleVec = angle.rotateRad(rotateAngle, rotateVec);

				danmaku.pos = pos.offset(angleVec, distance);
				danmaku.angle = angleVec;
				danmaku.roll = (float)(angle.pitch() * MathHelper.sin((float)rotateAngle));
				danmaku.rotation = danmaku.rotation.setRotationVec(rotateVec);

				EntityDanmaku spawned = danmaku.asEntity();
				set.add(spawned);
				danmaku.world.spawnEntityInWorld(spawned);
				rotateAngle += stepSize;
			}
		}

		return new Tuple<>(true, set);
	}
}
