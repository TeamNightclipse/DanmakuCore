/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.shape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ShapeHandler {

	private static final List<IShapeEntry> shapeList = new ArrayList<>();

	/**
	 * Creates a new shape with a position as an anchor
	 * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
	 */
	@SuppressWarnings("WeakerAccess")
	public static Set<EntityDanmaku> createShape(IShape shape, Vector3 pos, Quat orientation) {
		return createEntry(new ShapeEntryPosition(shape, pos, orientation));
	}

	/**
	 * Creates a new shape with an entity as an anchor
	 * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Set<EntityDanmaku> createShape(IShape shape, Entity anchor) {
		return createEntry(new ShapeEntryEntity(shape, anchor));
	}

	/**
	 * Creates a new shape with a living entity(eye height) as an anchor.
	 * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
	 */
	@SuppressWarnings("unused")
	public static Set<EntityDanmaku> createShape(IShape shape, EntityLivingBase anchor) {
		return createEntry(new ShapeEntryEntityLiving(shape, anchor));
	}

	/**
	 * Creates a new shape with a from the specific {@link ShapeEntry}
	 * @return A set that will contain all the danmaku spawned by the shape. The set's content will change over time
	 */
	@SuppressWarnings("WeakerAccess")
	public static Set<EntityDanmaku> createEntry(IShapeEntry entry) {
		shapeList.add(entry);
		return entry.getDrawn();
	}

	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			List<IShapeEntry> completedShapes = shapeList.stream().filter(IShapeEntry::draw).collect(Collectors.toList());
			shapeList.removeAll(completedShapes);
		}
	}

	@SuppressWarnings("WeakerAccess")
	public interface IShapeEntry {

		/**
		 * Gets the current drawn danmaku from this shape. This set should always be the same reference.
		 */
		Set<EntityDanmaku> getDrawn();

		/**
		 * Draws this shape, giving it the information it needs.
		 * @return If this shape is completed and should be removed.
		 */
		boolean draw();
	}

	/**
	 * A class that specifies how a shape should be drawn.
	 */
	private abstract static class ShapeEntry implements IShapeEntry {

		final Set<EntityDanmaku> drawn = new HashSet<>();
		final IShape shape;
		int counter = 0;

		ShapeEntry(IShape shape) {
			this.shape = shape;
		}

		@Override
		public Set<EntityDanmaku> getDrawn() {
			return drawn;
		}
	}

	private abstract static class ShapeEntryDynamicPos<T> extends ShapeEntry {

		@SuppressWarnings("WeakerAccess")
		protected final T dynPos;

		ShapeEntryDynamicPos(IShape shape, T dynPos) {
			super(shape);
			this.dynPos = dynPos;
		}

		@Override
		public boolean draw() {
			Vector3 pos = getCurrentPos();
			Quat orientation = getCurrentOrientation();

			ShapeResult ret = shape.drawForTick(pos, orientation, counter);
			drawn.addAll(ret.getSpawnedDanmaku());
			shape.doEffects(pos, orientation, counter, ret.getSpawnedDanmaku(), ImmutableSet.copyOf(drawn));

			counter++;
			return ret.isDone();
		}

		protected abstract Vector3 getCurrentPos();
		protected abstract Quat getCurrentOrientation();
	}

	private static class ShapeEntryEntity extends ShapeEntryDynamicPos<Entity> {

		ShapeEntryEntity(IShape shape, Entity dynPos) {
			super(shape, dynPos);
		}

		@Override
		protected Vector3 getCurrentPos() {
			return new Vector3(dynPos);
		}

		@Override
		protected Quat getCurrentOrientation() {
			return Quat.orientationOf(dynPos);
		}
	}

	private static class ShapeEntryEntityLiving extends ShapeEntryDynamicPos<EntityLivingBase> {

		ShapeEntryEntityLiving(IShape shape, EntityLivingBase dynPos) {
			super(shape, dynPos);
		}

		@Override
		protected Vector3 getCurrentPos() {
			return new Vector3(dynPos);
		}

		@Override
		protected Quat getCurrentOrientation() {
			return Quat.orientationOf(dynPos);
		}
	}

	private static class ShapeEntryPosition extends ShapeEntry {

		private final Vector3 pos;
		private final Quat orientation;

		ShapeEntryPosition(IShape shape, Vector3 pos, Quat orientation) {
			super(shape);
			this.pos = pos;
			this.orientation = orientation;
		}

		@Override
		public boolean draw() {
			ShapeResult ret = shape.drawForTick(pos, orientation, counter);
			drawn.addAll(ret.getSpawnedDanmaku());
			shape.doEffects(pos, orientation, counter, ret.getSpawnedDanmaku(), ImmutableSet.copyOf(drawn));

			counter++;
			return ret.isDone();
		}
	}
}
