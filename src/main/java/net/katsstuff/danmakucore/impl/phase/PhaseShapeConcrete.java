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
import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public abstract class PhaseShapeConcrete extends Phase {

	private static final String NBT_AMOUNT = "amount";
	private static final String NBT_BASE_ANGLE = "baseAngle";
	private static final String NBT_DISTANCE = "distance";
	private static final String NBT_SHOT_DATA = "shotData";
	private static final String NBT_MOVEMENT_DATA = "movementData";
	private static final String NBT_ROTATION_DATA = "rotationData";

	protected int amount;
	protected float baseAngle;
	protected double distance;
	protected ShotData shotData;
	protected MovementData movementData;
	protected RotationData rotationData;

	protected IShape shape;

	public PhaseShapeConcrete(PhaseManager manager, int amount, float baseAngle, double distance, ShotData shotData, MovementData movementData,
			RotationData rotationData) {
		super(manager);
		this.amount = amount;
		this.baseAngle = baseAngle;
		this.distance = distance;
		this.shotData = shotData;
		this.movementData = movementData;
		this.rotationData = rotationData;
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
			Vector3 entityPos = new Vector3(entity);
			Vector3 forward = new Vector3(target).subtract(entityPos);

			shape.drawForTick(entityPos, Quat.lookRotation(forward, Vector3.Up()), 0);
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
		baseAngle = tag.getFloat(NBT_BASE_ANGLE);
		distance = tag.getDouble(NBT_DISTANCE);
		shotData = new ShotData(tag.getCompoundTag(NBT_SHOT_DATA));
		movementData = MovementData.fromNBT(tag.getCompoundTag(NBT_MOVEMENT_DATA));
		rotationData = RotationData.fromNBT(tag.getCompoundTag(NBT_ROTATION_DATA));
	}
}
