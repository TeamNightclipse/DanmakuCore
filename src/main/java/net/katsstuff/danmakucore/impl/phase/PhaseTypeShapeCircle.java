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
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.impl.shape.ShapeCircle;
import net.minecraft.nbt.NBTTagCompound;

public class PhaseTypeShapeCircle extends PhaseType {

	@Override
	public Phase instantiate(PhaseManager phaseManager) {
		return new Circle(this, phaseManager, 8, 0F, 0.5D, ShotData.DefaultShotData(), MovementData.constant(0.4D), RotationData.none());
	}

	public Phase instantiate(PhaseManager phaseManager, int amount, float baseAngle, double distance, ShotData shotData,
			MovementData movementData, RotationData rotationData) {
		return new Circle(this, phaseManager, amount, baseAngle, distance, shotData, movementData, rotationData);
	}

	public static class Circle extends Phase {

		private static final String NBT_AMOUNT = "amount";
		private static final String NBT_BASE_ANGLE = "baseAngle";
		private static final String NBT_DISTANCE = "distance";
		private static final String NBT_SHOT_DATA = "shotData";
		private static final String NBT_MOVEMENT_DATA = "movementData";
		private static final String NBT_ROTATION_DATA = "rotationData";

		private final PhaseTypeShapeCircle type;
		private int amount;
		private float baseAngle;
		private double distance;
		private ShotData shotData;
		private MovementData movementData;
		private RotationData rotationData;

		private ShapeCircle shape;

		public Circle(PhaseTypeShapeCircle type, PhaseManager manager, int amount, float baseAngle, double distance, ShotData shotData,
				MovementData movementData, RotationData rotationData) {
			super(manager);
			this.type = type;
			this.amount = amount;
			this.baseAngle = baseAngle;
			this.distance = distance;
			this.shotData = shotData;
			this.movementData = movementData;
			this.rotationData = rotationData;

			DanmakuTemplate template = DanmakuTemplate.builder()
					.setUser(getEntity())
					.setShot(shotData)
					.setMovementData(movementData)
					.setRotationData(rotationData)
					.build();
			shape = new ShapeCircle(template, amount, baseAngle, distance);
		}

		@Override
		public PhaseType getType() {
			return type;
		}

		@Override
		public void init() {
			super.init();
			interval = 5;
		}

		@Override
		public void serverUpdate() {
			super.serverUpdate();
			if(isCounterStart()) {
				EntityDanmakuMob entity = getEntity();
				shape.drawForTick(new Vector3(entity), Vector3.angleEntity(entity), 0);
			}
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = super.serializeNBT();
			tag.setInteger(NBT_AMOUNT, amount);
			tag.setFloat(NBT_BASE_ANGLE, baseAngle);
			tag.setDouble(NBT_DISTANCE, distance);
			tag.setTag(NBT_SHOT_DATA, shotData.serializeNBT());
			tag.setTag(NBT_MOVEMENT_DATA, movementData.serializeNBT());
			tag.setTag(NBT_ROTATION_DATA, rotationData.serializeNBT());
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			super.deserializeNBT(tag);
			amount = tag.getInteger(NBT_AMOUNT);
			baseAngle = tag.getInteger(NBT_BASE_ANGLE);
			distance = tag.getDouble(NBT_DISTANCE);
			shotData = new ShotData(tag.getCompoundTag(NBT_SHOT_DATA));
			movementData = MovementData.fromNBT(tag.getCompoundTag(NBT_MOVEMENT_DATA));
			rotationData = RotationData.fromNBT(tag.getCompoundTag(NBT_ROTATION_DATA));

			DanmakuTemplate template = DanmakuTemplate.builder()
					.setUser(getEntity())
					.setShot(shotData)
					.setMovementData(movementData)
					.setRotationData(rotationData)
					.build();
			shape = new ShapeCircle(template, amount, baseAngle, distance);
		}
	}
}
