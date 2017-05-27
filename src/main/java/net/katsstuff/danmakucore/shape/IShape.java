/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.shape;

import java.util.Set;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.minecraft.util.Tuple;

/**
 * Something that can be drawn as danmaku over several ticks. Call {@link ShapeHandler#createShape} to create the shape.
 */
public interface IShape {

	/**
	 * Draws a shape for the given tick.
	 *
	 * @param pos The position to draw the shape at.
	 * @param orientation The direction that was registered for this shape.
	 * @param tick The tick position.
	 * @return Tuple first, if this shape is done. Tuple second, the danmaku that was created by this shape.
	 */
	Tuple<Boolean, Set<EntityDanmaku>> drawForTick(Vector3 pos, Quat orientation, int tick);
}
