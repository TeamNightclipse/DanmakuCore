/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

//EntityAIAttackRangedBow without attack part
public class EntityAIMoveRanged extends EntityAIBase {

	private final EntityLiving entity;
	private final double moveSpeedAmp;
	private final float maxAttackDistance;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public EntityAIMoveRanged(EntityLiving skeleton, double speedAmplifier, float maxDistance) {
		this.entity = skeleton;
		this.moveSpeedAmp = speedAmplifier;
		this.maxAttackDistance = maxDistance * maxDistance;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		return this.entity.getAttackTarget() != null;
	}

	public boolean continueExecuting() {
		return (this.shouldExecute() || !this.entity.getNavigator().noPath());
	}

	public void resetTask() {
		super.resetTask();
		this.seeTime = 0;
	}

	public void updateTask() {
		EntityLivingBase entitylivingbase = entity.getAttackTarget();

		if(entitylivingbase != null) {
			double d0 = entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
			boolean flag = entity.getEntitySenses().canSee(entitylivingbase);
			boolean flag1 = seeTime > 0;

			if(flag != flag1) {
				this.seeTime = 0;
			}

			if(flag) {
				++seeTime;
			}
			else {
				--seeTime;
			}

			if(d0 <= this.maxAttackDistance && seeTime >= 20) {
				entity.getNavigator().clearPathEntity();
				++strafingTime;
			}
			else {
				entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, moveSpeedAmp);
				strafingTime = -1;
			}

			if(strafingTime >= 20) {
				if(entity.getRNG().nextFloat() < 0.3D) {
					strafingClockwise = !strafingClockwise;
				}

				if(entity.getRNG().nextFloat() < 0.3D) {
					strafingBackwards = !strafingBackwards;
				}

				strafingTime = 0;
			}

			if(strafingTime > -1) {
				if(d0 > (maxAttackDistance * 0.75F)) {
					strafingBackwards = false;
				}
				else if(d0 < maxAttackDistance * 0.25F) {
					strafingBackwards = true;
				}

				entity.getMoveHelper().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
				entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
			}
			else {
				entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
			}
		}
	}
}
