/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.impl.shape.ShapeRing;
import net.minecraft.nbt.NBTTagCompound;

public class PhaseTypeShapeRing extends PhaseType {

	@Override
	public Phase instantiate(PhaseManager phaseManager) {
		return new Ring(this, phaseManager, 8, 2F, 0F, 0.5D, ShotData.DefaultShotData(), MovementData.constant(0.4D), RotationData.none());
	}

	public Phase instantiate(PhaseManager phaseManager, int amount, float radius, float baseAngle, double distance, ShotData shotData,
			MovementData movementData, RotationData rotationData) {
		return new Ring(this, phaseManager, amount, radius, baseAngle, distance, shotData, movementData, rotationData);
	}

	public static class Ring extends PhaseShapeConcrete {

		private static final String NBT_RADIUS = "radius";

		private final PhaseTypeShapeRing type;
		private float radius;

		public Ring(PhaseTypeShapeRing type, PhaseManager manager, int amount, float radius, float baseAngle, double distance, ShotData shotData,
				MovementData movementData, RotationData rotationData) {
			super(manager, amount, baseAngle, distance, shotData, movementData, rotationData);
			this.type = type;
			this.radius = radius;

			DanmakuTemplate template = DanmakuTemplate.builder()
					.setUser(getEntity())
					.setShot(shotData)
					.setMovementData(movementData)
					.setRotationData(rotationData)
					.build();
			shape = new ShapeRing(template, amount, radius, baseAngle, distance);
		}

		@Override
		public PhaseType getType() {
			return type;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = super.serializeNBT();
			tag.setFloat(NBT_RADIUS, radius);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			super.deserializeNBT(tag);
			radius = tag.getFloat(NBT_RADIUS);

			DanmakuTemplate template = DanmakuTemplate.builder()
					.setUser(getEntity())
					.setShot(shotData)
					.setMovementData(movementData)
					.setRotationData(rotationData)
					.build();
			shape = new ShapeRing(template, amount, radius, baseAngle, distance);
		}
	}
}
