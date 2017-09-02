/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */

package net.katsstuff.danmakucore.impl.shape;

import com.google.common.collect.ImmutableSet;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

@SuppressWarnings("unused")
public class ShapeSingle implements IShape {

	private final DanmakuTemplate danmaku;

	public ShapeSingle(DanmakuTemplate danmaku) {
		this.danmaku = danmaku;
	}

	@Override
	public ShapeResult draw(Vector3 pos, Quat orientation, int tick) {
		if(!danmaku.world.isRemote) {
			EntityDanmaku created = danmaku.asEntity();
			danmaku.world.spawnEntity(created);
			return ShapeResult.done(ImmutableSet.of(created));
		}
		else {
			return ShapeResult.done(ImmutableSet.of());
		}
	}
}
