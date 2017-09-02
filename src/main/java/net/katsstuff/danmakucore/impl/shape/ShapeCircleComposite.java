/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;
import net.minecraft.world.World;

public class ShapeCircleComposite implements IShape {

	private final World world;
	private final IShape shape;
	private final int amount;
	private float baseAngle;

	public ShapeCircleComposite(World world, IShape shape, int amount, float baseAngle) {
		this.world = world;
		this.shape = shape;
		this.amount = amount;
		this.baseAngle = baseAngle;
	}

	@Override
	public ShapeResult drawForTick(Vector3 pos, Quat orientation, int tick) {
		if(amount % 2 == 0) {
			baseAngle += 360F / (amount * 2F);
		}
		ShapeWideComposite wideShape = new ShapeWideComposite(world, shape, amount, 360F - 360F / amount, baseAngle);
		return wideShape.drawForTick(pos, orientation, tick);
	}
}
