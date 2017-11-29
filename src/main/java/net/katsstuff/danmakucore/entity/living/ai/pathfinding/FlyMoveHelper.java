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

	@Override
	public void onUpdateMoveHelper() {
		if (isEntityFlying()) {
			if ((this.action == EntityMoveHelper.Action.MOVE_TO) && !danmakuEntity.getNavigator().noPath()) {
				double dx = posX - danmakuEntity.posX;
				double dy = posY - danmakuEntity.posY;
				double dz = posZ - danmakuEntity.posZ;

				float dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				float f    = (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90F;

				dy = dy / dist;

				danmakuEntity.rotationYaw = limitAngle(danmakuEntity.rotationYaw, f, 90.0F);
				danmakuEntity.renderYawOffset = danmakuEntity.rotationYaw;
				float acceleration = (float)(speed * danmakuEntity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				this.danmakuEntity.setAIMoveSpeed(danmakuEntity.getAIMoveSpeed() + (acceleration - danmakuEntity.getAIMoveSpeed()) * 0.125F);

				float f2 = 0.0025F;
				float f3 = MathHelper.cos(danmakuEntity.rotationYaw * 0.017453292F);
				float f4 = MathHelper.sin(danmakuEntity.rotationYaw * 0.017453292F);

				danmakuEntity.motionX += f2 * f3;
				danmakuEntity.motionZ += f2 * f4;

				danmakuEntity.motionY += f2 * (f4 + f3) * 0.25D;
				danmakuEntity.motionY += danmakuEntity.getAIMoveSpeed() * dy * 0.1D;

				double d7 = danmakuEntity.posX + dx / dist * 2.0D;
				double d8 = danmakuEntity.getEyeHeight() + danmakuEntity.posY + dy / dist;
				double d9 = danmakuEntity.posZ + dz / dist * 2.0D;

				EntityLookHelper lookHelper = danmakuEntity.getLookHelper();
				double d10 = d7;
				double d11 = d8;
				double d12 = d9;

				if (lookHelper.getIsLooking()) {
					d10 = lookHelper.getLookPosX();
					d11 = lookHelper.getLookPosY();
					d12 = lookHelper.getLookPosZ();
				}

				danmakuEntity.getLookHelper().setLookPosition(
						d10 + (d7 - d10) * 0.125D,
						d11 + (d8 - d11) * 0.125D,
						d12 + (d9 - d12) * 0.125D,
						10.0F,
						40.0F
				);
			} else {
				this.danmakuEntity.setAIMoveSpeed(0.0F);
			}
		} else super.onUpdateMoveHelper();
	}

	private boolean isEntityFlying() {
		if(danmakuEntity instanceof EntityDanmakuMob) {
			return ((EntityDanmakuMob)danmakuEntity).isFlying();
		}
		else return danmakuEntity instanceof EntityDanmakuCreature && ((EntityDanmakuCreature)danmakuEntity).isFlying();
	}
}
