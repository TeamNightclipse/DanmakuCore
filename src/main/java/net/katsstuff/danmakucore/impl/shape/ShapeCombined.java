/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

@SuppressWarnings("unused")
public class ShapeCombined implements IShape {

	private final IShape[] shapes;

	public ShapeCombined(IShape... shapes) {
		this.shapes = Arrays.copyOf(shapes, shapes.length);
	}

	@Override
	public ShapeResult draw(Vector3 pos, Quat orientation, int tick) {
		Set<ShapeResult> ret = Arrays.stream(shapes).map(s -> s.draw(pos, orientation, tick)).collect(Collectors.toSet());
		boolean done = ret.stream().allMatch(ShapeResult::isDone);
		Set<EntityDanmaku> drawn = ret.stream().flatMap(t -> t.getSpawnedDanmaku().stream()).collect(Collectors.toSet());

		return ShapeResult.of(done, drawn);
	}
}
