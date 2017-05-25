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

import net.katsstuff.danmakucore.data.Mat4;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

public class ShapeWideComposite implements IShape {

	private final World world;
	private final IShape shape;
	private final int amount;
	private final float wideAngle;
	private final float baseAngle;
	private final double distance;
	private final Set<EntityDanmaku> set = new HashSet<>();

	public ShapeWideComposite(World world, IShape shape, int amount, float wideAngle, float baseAngle, double distance) {
		this.world = world;
		this.shape = shape;
		this.amount = amount;
		this.wideAngle = wideAngle;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		boolean done = true;

		if(!world.isRemote) {
			Mat4 fromWorld = Mat4.fromWorld(pos, angle, Vector3.Up());
			Vector3 rotateVec = angle.rotate(90, Vector3.Left().transformDirection(fromWorld));

			double rotateAngle = Math.toRadians(-wideAngle / 2F);
			double stepSize = Math.toRadians(wideAngle / (amount - 1));
			rotateAngle += Math.toRadians(baseAngle);

			for(int i = 0; i < amount; i++) {
				Vector3 angleVec = angle.rotateRad(rotateAngle, rotateVec);

				Tuple<Boolean, Set<EntityDanmaku>> result = shape.drawForTick(pos.offset(angleVec, distance), angleVec, tick);

				set.addAll(result.getSecond());
				if(!result.getFirst()) {
					done = false;
				}
				rotateAngle += stepSize;
			}
		}

		return new Tuple<>(done, set);
	}
}
