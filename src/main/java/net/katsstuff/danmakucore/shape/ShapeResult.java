package net.katsstuff.danmakucore.shape;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;

/**
 * The result of a shape drawing for a single tick.
 */
public class ShapeResult {

	private final boolean done;
	private final Set<EntityDanmaku> spawnedDanmaku;

	private ShapeResult(boolean done, Set<EntityDanmaku> spawnedDanmaku) {
		this.done = done;
		this.spawnedDanmaku = ImmutableSet.copyOf(spawnedDanmaku);
	}

	/**
	 * Creates a result signifying that the shape is done.
	 * @param spawnedDanmaku The danmaku the shape spawned for this tick.
	 */
	public static ShapeResult done(Set<EntityDanmaku> spawnedDanmaku) {
		return new ShapeResult(true, spawnedDanmaku);
	}

	/**
	 * Creates a result signifying that the shape is not done.
	 * @param spawnedDanmaku The danmaku the shape spawned for this tick.
	 */
	public static ShapeResult notDone(Set<EntityDanmaku> spawnedDanmaku) {
		return new ShapeResult(false, spawnedDanmaku);
	}

	/**
	 * Creates a result of s drawn shape. If possible, prefer one of the other static methods.
	 */
	public static ShapeResult of(boolean done, Set<EntityDanmaku> drawn) {
		return new ShapeResult(done, drawn);
	}

	/**
	 * If this shape has finished executing.
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * The danmaku the shape spawned for this tick.
	 */
	public Set<EntityDanmaku> getSpawnedDanmaku() {
		return spawnedDanmaku;
	}

	@Override
	public String toString() {
		return "ShapeResult{" + "done=" + done + ", spawnedDanmaku=" + spawnedDanmaku + '}';
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		ShapeResult that = (ShapeResult)o;

		return done == that.done && spawnedDanmaku.equals(that.spawnedDanmaku);
	}

	@Override
	public int hashCode() {
		int result = (done ? 1 : 0);
		result = 31 * result + spawnedDanmaku.hashCode();
		return result;
	}
}
