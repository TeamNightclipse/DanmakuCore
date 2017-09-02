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

@SuppressWarnings("unused")
public class ShapeArrow implements IShape {

	private final int amount;
	private final double distance;
	private final double width;
	private final DanmakuTemplate danmaku;

	/**
	 * Creates a {@link ShapeArrow}
	 * @param danmaku The danmaku to use
	 * @param amount How many danmaku should appear on each side - 1
	 * @param distance How much depth should be between each wave of danmaku.
	 * @param width How much width should there be between two waves of danmaku.
	 */
	public ShapeArrow(DanmakuTemplate danmaku, int amount, double distance, double width) {
		this.danmaku = danmaku;
		this.amount = amount;
		this.distance = distance;
		this.width = width;
	}

	@Override
	public ShapeResult draw(Vector3 pos, Quat orientation, int tick) {
		HashSet<EntityDanmaku> set = new HashSet<>();
		if(!danmaku.world.isRemote) {
			Vector3 localForward = Vector3.Forward().rotate(orientation);
			Vector3 localLeft = Vector3.Left().rotate(orientation);
			Vector3 localRight = Vector3.Right().rotate(orientation);
			danmaku.direction = localForward;

			for(int i = 0; i < amount; i++) {
				double newDistance = -i * distance;
				double newWidth = -i * width;
				Vector3 newPosNeutral = pos.offset(localForward, newDistance);

				danmaku.pos = newPosNeutral.offset(localLeft, newWidth);
				EntityDanmaku createdLeft = danmaku.asEntity();
				danmaku.world.spawnEntity(createdLeft);
				set.add(createdLeft);

				danmaku.pos = newPosNeutral.offset(localRight, newWidth);
				EntityDanmaku createdRight = danmaku.asEntity();
				danmaku.world.spawnEntity(createdRight);
				set.add(createdRight);
			}
		}
		return ShapeResult.done(set);
	}
}
