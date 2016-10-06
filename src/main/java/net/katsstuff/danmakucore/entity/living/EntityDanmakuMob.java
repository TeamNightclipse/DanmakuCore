/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living;

import net.katsstuff.danmakucore.EnumDanmakuLevel;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DamageSourceDanmaku;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeSpellcard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

//Some code taken from EntityWither
@SuppressWarnings("WeakerAccess")
public abstract class EntityDanmakuMob extends EntityMob {

	private static final String NBT_FLYINGHEIGHT = "flyingHeight";
	private static final String NBT_PHASE_MANAGER = "phaseManager";

	protected final PhaseManager phaseManager;

	private int flyingHeight;
	private EnumSpecies species;

	public EntityDanmakuMob(World world) {
		super(world);
		setFlyingHeight(3);
		setSpecies(EnumSpecies.OTHERS);
		phaseManager = new PhaseManager(this);
		((PathNavigateGround)this.getNavigator()).setCanSwim(true);
	}

	/*
	@Override
	protected PathNavigate getNewNavigator(World worldIn) {
		return new PathNavigateFlyer(this, worldIn);
	}
	*/

	@Override
	public void onLivingUpdate() {
		if(isFlying()) {
			this.motionY *= 0.6D;

			Entity target = getAttackTarget();
			if(!worldObj.isRemote && target != null) {
				if(posY < target.posY) {
					if(motionY < 0.0D) {
						motionY = 0.0D;
					}

					motionY += (0.5D - motionY) * 0.6D;
				}

				double distanceX = target.posX - this.posX;
				double distanceY = target.posZ - this.posZ;
				double distanceSquared = distanceX * distanceX + distanceY * distanceY;

				if(distanceSquared > 9.0D) {
					double distance = Math.sqrt(distanceSquared);
					motionX += (distanceX / distance * 0.5D - motionX) * 0.6D;
					motionZ += (distanceY / distance * 0.5D - motionZ) * 0.6D;
				}
			}

			if(motionX * motionX + motionZ * motionZ > 0.05D) {
				rotationYaw = (float)(Math.toDegrees(MathHelper.atan2(motionZ, motionX)) - 90);
				//this.rotationYaw = (float)MathHelper.atan2(motionZ, motionX) * (180F / (float)Math.PI) - 90.0F;
			}
		}

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		if(ConfigHandler.getDanmakuLevel() == EnumDanmakuLevel.PEACEFUL) {
			setAttackTarget(null);
		}

		super.onUpdate();
		phaseManager.tick();
	}

	//TODO: Does this need to be here anymore?
	/*
	protected void hover() {
		int heightCount = 0;
		while(worldObj.isAirBlock(new BlockPos(posX, posY - heightCount, posZ)) && heightCount < 8) {
			heightCount++;
		}

		if(getAttackTarget() != null && heightCount > getFlyingHeight()) {
			double distance = getAttackTarget().posY - posY;
			motionY += distance * 0.0006D;
		}
		else {
			if(heightCount > getFlyingHeight()) {
				motionY -= 0.006D;
			}
			else if(heightCount < getFlyingHeight()) {
				motionY += 0.006D;
			}
		}
		moveEntity(0D, motionY, 0D);
	}
	*/

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float damage) {
		if(!(damageSource instanceof DamageSourceDanmaku)) {
			damage *= 0.25F;
		}

		return super.attackEntityFrom(damageSource, damage);
	}

	/*
	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if(isFlying()) {
			if(isInWater()) {
				moveRelative(strafe, forward, 0.02F);
				moveEntity(motionX, motionY, motionZ);
				motionX *= 0.8D;
				motionY *= 0.8D;
				motionZ *= 0.8D;
			}
			else if(isInLava()) {
				moveRelative(strafe, forward, 0.02F);
				moveEntity(motionX, motionY, motionZ);
				motionX *= 0.5D;
				motionY *= 0.5D;
				motionZ *= 0.5D;
			}
			else {
				float f = 0.91F;

				if(onGround) {
					f = worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(getEntityBoundingBox().minY) - 1,
							MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91F;
				}

				float f1 = 0.16277136F / (f * f * f);
				moveRelative(strafe, forward, onGround ? 0.1F * f1 : 0.02F);
				f = 0.91F;

				if(onGround) {
					f = worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(getEntityBoundingBox().minY) - 1,
							MathHelper.floor_double(posZ))).getBlock().slipperiness * 0.91F;
				}

				moveEntity(motionX, motionY, motionZ);
				motionX *= f;
				motionY *= f;
				motionZ *= f;
			}

			prevLimbSwingAmount = limbSwingAmount;
			double d1 = posX - prevPosX;
			double d0 = posZ - prevPosZ;
			float f2 = MathHelper.sqrt_double(d1 * d1 + d0 * d0) * 4.0F;

			if(f2 > 1.0F) {
				f2 = 1.0F;
			}

			limbSwingAmount += (f2 - limbSwingAmount) * 0.4F;
			limbSwing += limbSwingAmount;
		}
		else {
			super.moveEntityWithHeading(strafe, forward);
		}
	}
	*/

	@Override
	public boolean isOnLadder() {
		return !isFlying() && super.isOnLadder();
	}

	@Override
	protected void onDeathUpdate() {
		super.onDeathUpdate();

		if(deathTime == 7) {
			DanmakuHelper.explosionEffect2(worldObj, pos(), 1.0F + deathTime * 0.1F);
			DanmakuHelper.chainExplosion(this, 5.0F, 5.0F);
		}

	}

	public int getFlyingHeight() {
		return flyingHeight;
	}

	public void setFlyingHeight(int flyingHeight) {
		this.flyingHeight = flyingHeight;
	}

	public EnumSpecies getSpecies() {
		return species;
	}

	protected void setSpecies(EnumSpecies species) {
		this.species = species;
	}

	public boolean isFlying() {
		return getFlyingHeight() > 0;
	}

	protected void setMaxHP(float hp) {
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(hp);
	}

	public void setSpeed(double speed) {
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
	}

	public double getSpeed() {
		return getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
	}

	public Vector3 pos() {
		return new Vector3(this);
	}

	@Override
	protected void dropFewItems(boolean hasBeenAttackedByPlayer, int lootingLevel) {
		Phase phase = phaseManager.getCurrentPhase();
		//TODO: Better/more stuff here
		if(phase instanceof PhaseTypeSpellcard.PhaseSpellcard && hasBeenAttackedByPlayer) {
			entityDropItem(((PhaseTypeSpellcard.PhaseSpellcard)phase).getItemStack(), 0F);
		}
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		if(!isFlying()) {
			super.fall(distance, damageMultiplier);
		}
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		if(!isFlying()) {
			super.updateFallState(y, onGroundIn, state, pos);
		}
	}

	@Override
	public int getMaxFallHeight() {
		if(isFlying()) return getFlyingHeight() * 4;
		return super.getMaxFallHeight();
	}

	@Override
	protected float getSoundVolume() {
		return 0.3F;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 1.95F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_BAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BAT_DEATH;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		setFlyingHeight(tag.getByte(NBT_FLYINGHEIGHT));
		phaseManager.deserializeNBT(tag.getCompoundTag(NBT_PHASE_MANAGER));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setByte(NBT_FLYINGHEIGHT, (byte)getFlyingHeight());
		tag.setTag(NBT_PHASE_MANAGER, phaseManager.serializeNBT());
	}
}
