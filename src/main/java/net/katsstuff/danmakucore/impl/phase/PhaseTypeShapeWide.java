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
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.impl.shape.ShapeRing;
import net.katsstuff.danmakucore.impl.shape.ShapeWideShot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class PhaseTypeShapeWide extends PhaseType {

	@Override
	public Phase instantiate(PhaseManager phaseManager) {
		return new Ring(this, phaseManager, 8, 30F, 0F, 0.5D, ShotData.DefaultShotData(), MovementData.constant(0.4D), RotationData.none());
	}

	public Phase instantiate(PhaseManager phaseManager, int amount, float wideAngle, float baseAngle, double distance, ShotData shotData,
			MovementData movementData, RotationData rotationData) {
		return new Ring(this, phaseManager, amount, wideAngle, baseAngle, distance, shotData, movementData, rotationData);
	}

	public static class Ring extends Phase {

		private static final String NBT_AMOUNT = "amount";
		private static final String NBT_WIDE_ANGLE = "wideAngle";
		private static final String NBT_BASE_ANGLE = "baseAngle";
		private static final String NBT_DISTANCE = "distance";
		private static final String NBT_SHOT_DATA = "shotData";
		private static final String NBT_MOVEMENT_DATA = "movementData";
		private static final String NBT_ROTATION_DATA = "rotationData";

		private final PhaseTypeShapeWide type;
		private int amount;
		private float wideAngle;
		private float baseAngle;
		private double distance;
		private ShotData shotData;
		private MovementData movementData;
		private RotationData rotationData;

		private ShapeWideShot shape;

		public Ring(PhaseTypeShapeWide type, PhaseManager manager, int amount, float wideAngle, float baseAngle, double distance, ShotData shotData,
				MovementData movementData, RotationData rotationData) {
			super(manager);
			this.type = type;
			this.amount = amount;
			this.wideAngle = wideAngle;
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
			shape = new ShapeWideShot(template, amount, wideAngle, baseAngle, distance);
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

			EntityDanmakuMob entity = getEntity();
			EntityLivingBase target = entity.getAttackTarget();

			if(!isFrozen() && isCounterStart() && target != null && entity.getEntitySenses().canSee(target)) {
				shape.drawForTick(new Vector3(entity), Vector3.angleToEntity(entity, target), 0);
			}
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = super.serializeNBT();
			tag.setInteger(NBT_AMOUNT, amount);
			tag.setFloat(NBT_WIDE_ANGLE, wideAngle);
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
			wideAngle = tag.getFloat(NBT_WIDE_ANGLE);
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
			shape = new ShapeWideShot(template, amount, wideAngle, baseAngle, distance);
		}
	}
}
