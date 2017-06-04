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
		this.bands = bands / 2;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Quat orientation, int tick) {
		if(!danmaku.world.isRemote) {
			Quat rotatedForward = orientation.multiply(Quat.fromAxisAngle(Vector3.Forward(), 90));
			float increment = 180F / bands;
			ShapeCircle shape = new ShapeCircle(danmaku, rings, baseAngle, distance);

			for(int i = 0; i < bands; i++) {
				Quat rotate = Quat.fromAxisAngle(Vector3.Up(), increment * i);
				set.addAll(shape.drawForTick(pos, rotate.multiply(rotatedForward), tick).getSecond());
			}
		}

		return new Tuple<>(true, set);
	}
}
