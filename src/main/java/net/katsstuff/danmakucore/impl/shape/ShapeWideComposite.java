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
	private final Set<EntityDanmaku> set = new HashSet<>();

	public ShapeWideComposite(World world, IShape shape, int amount, float wideAngle, float baseAngle) {
		this.world = world;
		this.shape = shape;
		this.amount = amount;
		this.wideAngle = wideAngle;
		this.baseAngle = baseAngle;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Quat orientation, int tick) {
		boolean done = true;

		if(!world.isRemote) {
			double rotateAngle = -wideAngle / 2D;
			double stepSize = wideAngle / (amount - 1);
			rotateAngle += baseAngle;

			for(int i = 0; i < amount; i++) {
				Quat rotate = orientation.multiply(Quat.fromAxisAngle(Vector3.Up(), rotateAngle));
				Tuple<Boolean, Set<EntityDanmaku>> result = shape.drawForTick(pos, rotate, tick);
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
