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
import net.katsstuff.danmakucore.entity.danmaku.DanmakuBuilder;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

public class ShapeSphere implements IShape {

	private final DanmakuBuilder danmaku;
	private final int rings;
	private final int bands;
	private final float baseAngle;
	private final double distance;
	private final Set<EntityDanmaku> set = new HashSet<>();

	public ShapeSphere(DanmakuBuilder danmaku, int rings, int bands, float baseAngle, double distance) {
		this.danmaku = danmaku;
		this.rings = rings;
		this.bands = bands;
		this.baseAngle = baseAngle;
		this.distance = distance;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		if(!danmaku.world.isRemote) {
			Vector3 rotateVec = Vector3.fromSpherical(angle.yaw(), angle.pitch() + 90); //TODO: How do do this without relying on yaw and pitch?
			/*
			for(int i = 0; i < bands; i++) {
				ShapeWideShot shape = new ShapeWideShot(danmaku, rings, 360F, baseAngle, distance);
				set.addAll(shape.drawForTick(pos, angle.rotate(360F / i, rotateVec), tick).getSecond());
			}
			*/
		}

		return new Tuple<>(true, set);
	}
}
