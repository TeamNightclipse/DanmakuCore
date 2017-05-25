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
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

public class ShapeSphere implements IShape {

	private final DanmakuTemplate danmaku;
	private final int rings;
	private final int bands;
	private final float baseAngle;
	private final double distance;
	private final Set<EntityDanmaku> set = new HashSet<>();

	public ShapeSphere(DanmakuTemplate danmaku, int rings, int bands, float baseAngle, double distance) {
		this.danmaku = danmaku;
		this.rings = rings;
		this.bands = bands;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		if(!danmaku.world.isRemote) {
			Mat4 fromWorld = Mat4.fromWorld(pos, angle, Vector3.Up());
			Vector3 localForward = Vector3.Forward().transformDirection(fromWorld);
			Vector3 localBackward = Vector3.Backward().transformDirection(fromWorld);
			Vector3 rotateVec = Vector3.Left().transformDirection(fromWorld);
			float increment = 180F / bands;
			ShapeWideShot shape = new ShapeWideShot(danmaku, rings, 180F, baseAngle, distance);

			for(int i = 0; i < bands; i++) {
				set.addAll(shape.drawForTick(pos, localForward.rotate(increment * i, rotateVec), tick).getSecond());
				set.addAll(shape.drawForTick(pos, localBackward.rotate(increment * i, rotateVec), tick).getSecond());
			}
		}

		return new Tuple<>(true, set);
	}
}
