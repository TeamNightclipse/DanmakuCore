/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper;

import java.util.Set;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.impl.shape.ShapeCircle;
import net.katsstuff.danmakucore.impl.shape.ShapeRandomRing;
import net.katsstuff.danmakucore.impl.shape.ShapeRing;
import net.katsstuff.danmakucore.impl.shape.ShapeSphere;
import net.katsstuff.danmakucore.impl.shape.ShapeStar;
import net.katsstuff.danmakucore.impl.shape.ShapeWide;
import net.katsstuff.danmakucore.shape.IShape;

/**
 * A few helper methods for the most used shapes.
 */
@SuppressWarnings("UnusedReturnValue")
public class DanmakuCreationHelper {

	public static Set<EntityDanmaku> createWideShot(Quat orientation, DanmakuTemplate danmaku, int amount, float wideAngle, float baseAngle,
			double distance) {
		return drawSingle(danmaku, orientation, new ShapeWide(danmaku, amount, wideAngle, baseAngle, distance));
	}

	public static Set<EntityDanmaku> createCircleShot(Quat orientation, DanmakuTemplate danmaku, int amount, float baseAngle, double distance) {
		return drawSingle(danmaku, orientation, new ShapeCircle(danmaku, amount, baseAngle, distance));
	}

	public static Set<EntityDanmaku> createRingShot(Quat orientation, DanmakuTemplate danmaku, int amount, float size, float baseAngle,
			double distance) {
		return drawSingle(danmaku, orientation, new ShapeRing(danmaku, amount, size, baseAngle, distance));
	}

	public static Set<EntityDanmaku> createRandomRingShot(Quat orientation, DanmakuTemplate danmaku, int amount, float size, double distance) {
		return drawSingle(danmaku, orientation, new ShapeRandomRing(danmaku, amount, size, distance));
	}

	public static Set<EntityDanmaku> createStarShot(Quat orientation, DanmakuTemplate danmaku, int amount, float angleZ, float baseAngle,
			double distance) {
		return drawSingle(danmaku, orientation, new ShapeStar(danmaku, amount, angleZ, baseAngle, distance));
	}

	public static Set<EntityDanmaku> createSphereShot(Quat orientation, DanmakuTemplate danmaku, int rings, int bands, float baseAngle,
			double distance) {
		return drawSingle(danmaku, orientation, new ShapeSphere(danmaku, rings, bands, baseAngle, distance));
	}

	private static Set<EntityDanmaku> drawSingle(DanmakuTemplate danmaku, Quat orientation, IShape shape) {
		return shape.draw(danmaku.pos, orientation, 0).getSpawnedDanmaku();
	}
}
