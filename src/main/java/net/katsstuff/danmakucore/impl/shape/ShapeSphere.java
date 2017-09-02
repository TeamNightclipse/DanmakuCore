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

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

public class ShapeSphere implements IShape {

	private final DanmakuTemplate danmaku;
	private final int rings;
	private final int bands;
	private final float baseAngle;
	private final double distance;

	public ShapeSphere(DanmakuTemplate danmaku, int rings, int bands, float baseAngle, double distance) {
		this.danmaku = danmaku;
		this.rings = rings;
		this.bands = bands / 2;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public ShapeResult draw(Vector3 pos, Quat orientation, int tick) {
		HashSet<EntityDanmaku> set = new HashSet<>();
		if(!danmaku.world.isRemote) {
			Quat rotatedForward = orientation.multiply(Quat.fromAxisAngle(Vector3.Forward(), 90));
			float increment = 180F / bands;
			ShapeCircle shape = new ShapeCircle(danmaku, rings, baseAngle, distance);

			for(int i = 0; i < bands; i++) {
				Quat rotate = Quat.fromAxisAngle(Vector3.Up(), increment * i);
				set.addAll(shape.draw(pos, rotate.multiply(rotatedForward), tick).getSpawnedDanmaku());
			}
		}

		return ShapeResult.done(set);
	}
}
