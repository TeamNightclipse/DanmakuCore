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

/**
 * Something that can be drawn as danmaku over several ticks. Call {@link ShapeHandler#createShape} to create the shape.
 */
@FunctionalInterface
public interface IShape {

	/**
	 * Draws a shape for the given tick.
	 *
	 * @param pos The position to draw the shape at.
	 * @param orientation The orientation that was registered for this shape.
	 * @param tick The tick.
	 * @return Tuple first, if this shape is done. Tuple second, the danmaku that was created by this shape.
	 */
	ShapeResult draw(Vector3 pos, Quat orientation, int tick);

	/**
	 * Executes some sort of effect on the danmaku spawned. This can be used
	 * as a cheaper {@link net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity}.
	 *
	 * @param pos The position the shape was drawn at.
	 * @param orientation The orientation that was registered for this shape.
	 * @param tick The tick.
	 * @param spawnedThisTick The danmaku that the shape spawned this tick.
	 * @param allSpawned All the danmaku that this shape has spawned so far.
	 */
	default void doEffects(Vector3 pos, Quat orientation, int tick, Set<EntityDanmaku> spawnedThisTick,
			Set<EntityDanmaku> allSpawned) {} //Do nothing by default
}
