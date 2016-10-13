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

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

@SuppressWarnings("unused")
public class ShapeCombined implements IShape {

	private final IShape[] shapes;

	public ShapeCombined(IShape... shapes) {
		this.shapes = Arrays.copyOf(shapes, shapes.length);
	}

	@Override
	public Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Vector3 angle, int tick) {
		Set<Tuple<Boolean, Set<EntityDanmaku>>> ret = Arrays.stream(shapes).map(s -> s.drawForTick(pos, angle, tick)).collect(Collectors.toSet());
		boolean done = ret.stream().allMatch(Tuple::getFirst);
		Set<EntityDanmaku> drawn = ret.stream()
				.flatMap(t -> t.getSecond().stream())
				.collect(Collectors.toSet());

		return new Tuple<>(done, drawn);
	}
}
