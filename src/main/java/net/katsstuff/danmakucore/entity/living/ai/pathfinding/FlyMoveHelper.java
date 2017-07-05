/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai.pathfinding;

import net.katsstuff.danmakucore.entity.living.EntityDanmakuCreature;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

//Some code taken from EntityGuardian.GuardianMoveHelper
public class FlyMoveHelper extends EntityMoveHelper {

	private final EntityCreature danmakuEntity;

	public FlyMoveHelper(EntityDanmakuMob danmakuMob) {
		super(danmakuMob);
		this.danmakuEntity = danmakuMob;
	}

	public FlyMoveHelper(EntityDanmakuCreature danmakuCreature) {
		super(danmakuCreature);
		this.danmakuEntity = danmakuCreature;
	}

	public void onUpdateMoveHelper() {
		if(isEntityFlying()) {
			if(this.action == EntityMoveHelper.Action.MOVE_TO && !danmakuEntity.getNavigator().noPath()) {
				double d0 = this.posX - this.danmakuEntity.posX;
				double d1 = this.posY - this.danmakuEntity.posY;
				double d2 = this.posZ - this.danmakuEntity.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = MathHelper.sqrt(d3);
				d1 = d1 / d3;
				float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				this.danmakuEntity.rotationYaw = this.limitAngle(this.danmakuEntity.rotationYaw, f, 90.0F);
				this.danmakuEntity.renderYawOffset = this.danmakuEntity.rotationYaw;
				float f1 = (float)(this.speed * this.danmakuEntity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				this.danmakuEntity.setAIMoveSpeed(this.danmakuEntity.getAIMoveSpeed() + (f1 - this.danmakuEntity.getAIMoveSpeed()) * 0.125F);
				double d4 = MathHelper.sin((float)((this.danmakuEntity.ticksExisted + this.danmakuEntity.getEntityId()) * 0.5D)) * 0.05D;
				double d5 = MathHelper.cos((this.danmakuEntity.rotationYaw * 0.017453292F));
				double d6 = MathHelper.sin((this.danmakuEntity.rotationYaw * 0.017453292F));
				this.danmakuEntity.motionX += d4 * d5;
				this.danmakuEntity.motionZ += d4 * d6;
				d4 = MathHelper.sin((float)((this.danmakuEntity.ticksExisted + this.danmakuEntity.getEntityId()) * 0.75D)) * 0.05D;
				this.danmakuEntity.motionY += d4 * (d6 + d5) * 0.25D;
				this.danmakuEntity.motionY += this.danmakuEntity.getAIMoveSpeed() * d1 * 0.1D;
				EntityLookHelper entitylookhelper = this.danmakuEntity.getLookHelper();
				double d7 = this.danmakuEntity.posX + d0 / d3 * 2.0D;
				double d8 = this.danmakuEntity.getEyeHeight() + this.danmakuEntity.posY + d1 / d3;
				double d9 = this.danmakuEntity.posZ + d2 / d3 * 2.0D;
				double d10 = entitylookhelper.getLookPosX();
				double d11 = entitylookhelper.getLookPosY();
				double d12 = entitylookhelper.getLookPosZ();

				if(!entitylookhelper.getIsLooking()) {
					d10 = d7;
					d11 = d8;
					d12 = d9;
				}

				this.danmakuEntity.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D,
						10.0F, 40.0F);
			}
			else {
				this.danmakuEntity.setAIMoveSpeed(0.0F);
			}
		}
		else super.onUpdateMoveHelper();
	}

	private boolean isEntityFlying() {
		if(danmakuEntity instanceof EntityDanmakuMob) {
			return ((EntityDanmakuMob)danmakuEntity).isFlying();
		}
		else return danmakuEntity instanceof EntityDanmakuCreature && ((EntityDanmakuCreature)danmakuEntity).isFlying();
	}
}
