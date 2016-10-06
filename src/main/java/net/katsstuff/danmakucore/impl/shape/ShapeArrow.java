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
import net.katsstuff.danmakucore.entity.danmaku.DanmakuBuilder;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

public class ShapeArrow implements IShape {

	private static final Quat ROTATE_LEFT = Quat.fromVector(new Vector3(0, 1, 0), 90);
	private static final Quat ROTATE_RIGHT = Quat.fromVector(new Vector3(0, 1, 0), -90);

	private final int amount;
	private final double distance;
	private final double width;
	private final DanmakuBuilder danmaku;
	private final Set<EntityDanmaku> set = new HashSet<>();

	/**
	 * Creates a {@link ShapeArrow}
	 * @param danmaku The danmaku to use
	 * @param amount How many danmaku should appear on each side - 1
	 * @param distance How much depth should be between each wave of danmaku.
	 * @param width How much width should there be between two waves of danmaku.
	 */
	public ShapeArrow(DanmakuBuilder danmaku, int amount, double distance, double width) {
		this.danmaku = danmaku;
		this.amount = amount;
		this.distance = distance;
		this.width = width;
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		if(!danmaku.world.isRemote) {
			Vector3 rotationVec = Vector3.fromSpherical(angle.yaw(), angle.pitch() + 90);
			Vector3 leftVec = angle.rotate(-90D, rotationVec);
			Vector3 rightVec = angle.rotate(90D, rotationVec);
			danmaku.angle = angle;

			for(int i = 0; i < amount; i++) {
				double newDistance = -i * distance;
				double newWidth = -i * width;
				Vector3 newPosNeutral = pos.offset(angle, newDistance);

				danmaku.pos = newPosNeutral.offset(leftVec, newWidth);
				EntityDanmaku createdLeft = danmaku.asEntity();
				danmaku.world.spawnEntityInWorld(createdLeft);
				set.add(createdLeft);

				danmaku.pos = newPosNeutral.offset(rightVec, newWidth);
				EntityDanmaku createdRight = danmaku.asEntity();
				danmaku.world.spawnEntityInWorld(createdRight);
				set.add(createdRight);
			}
		}
		return new Tuple<>(true, set);
	}
}
