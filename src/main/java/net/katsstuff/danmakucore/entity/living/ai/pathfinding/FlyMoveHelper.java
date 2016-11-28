/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai.pathfinding;

import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

//Some code taken from EntityGuardian.GuardianMoveHelper
public class FlyMoveHelper extends EntityMoveHelper {

	private final EntityDanmakuMob danmakuMob;

	public FlyMoveHelper(EntityDanmakuMob danmakuMob) {
		super(danmakuMob);
		this.danmakuMob = danmakuMob;
	}

	public void onUpdateMoveHelper() {
		if(danmakuMob.isFlying()) {
			if(this.action == EntityMoveHelper.Action.MOVE_TO && !danmakuMob.getNavigator().noPath()) {
				double d0 = this.posX - this.danmakuMob.posX;
				double d1 = this.posY - this.danmakuMob.posY;
				double d2 = this.posZ - this.danmakuMob.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = MathHelper.sqrt_double(d3);
				d1 = d1 / d3;
				float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				this.danmakuMob.rotationYaw = this.limitAngle(this.danmakuMob.rotationYaw, f, 90.0F);
				this.danmakuMob.renderYawOffset = this.danmakuMob.rotationYaw;
				float f1 = (float)(this.speed * this.danmakuMob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				this.danmakuMob.setAIMoveSpeed(this.danmakuMob.getAIMoveSpeed() + (f1 - this.danmakuMob.getAIMoveSpeed()) * 0.125F);
				double d4 = MathHelper.sin((float)((this.danmakuMob.ticksExisted + this.danmakuMob.getEntityId()) * 0.5D)) * 0.05D;
				double d5 = MathHelper.cos((this.danmakuMob.rotationYaw * 0.017453292F));
				double d6 = MathHelper.sin((this.danmakuMob.rotationYaw * 0.017453292F));
				this.danmakuMob.motionX += d4 * d5;
				this.danmakuMob.motionZ += d4 * d6;
				d4 = MathHelper.sin((float)((this.danmakuMob.ticksExisted + this.danmakuMob.getEntityId()) * 0.75D)) * 0.05D;
				this.danmakuMob.motionY += d4 * (d6 + d5) * 0.25D;
				this.danmakuMob.motionY += this.danmakuMob.getAIMoveSpeed() * d1 * 0.1D;
				EntityLookHelper entitylookhelper = this.danmakuMob.getLookHelper();
				double d7 = this.danmakuMob.posX + d0 / d3 * 2.0D;
				double d8 = this.danmakuMob.getEyeHeight() + this.danmakuMob.posY + d1 / d3;
				double d9 = this.danmakuMob.posZ + d2 / d3 * 2.0D;
				double d10 = entitylookhelper.getLookPosX();
				double d11 = entitylookhelper.getLookPosY();
				double d12 = entitylookhelper.getLookPosZ();

				if(!entitylookhelper.getIsLooking()) {
					d10 = d7;
					d11 = d8;
					d12 = d9;
				}

				this.danmakuMob.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D,
						10.0F, 40.0F);
			}
			else {
				this.danmakuMob.setAIMoveSpeed(0.0F);
			}
		}
		else super.onUpdateMoveHelper();
	}
}
