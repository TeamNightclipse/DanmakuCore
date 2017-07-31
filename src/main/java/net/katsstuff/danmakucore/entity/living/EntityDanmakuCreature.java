/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living;

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.living.ai.pathfinding.FlyMoveHelper;
import net.katsstuff.danmakucore.entity.living.ai.pathfinding.PathNavigateFlyer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDanmakuCreature extends EntityCreature {

	private static final String NBT_FLYINGHEIGHT = "flyingHeight";

	private int flyingHeight;
	private TouhouSpecies species;

	public EntityDanmakuCreature(World world) {
		super(world);
		moveHelper = new FlyMoveHelper(this);
		setSpecies(TouhouSpecies.OTHERS);
	}

	@Override
	protected PathNavigate createNavigator(World world) {
		return PathNavigateFlyer.create(this, world);
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if(isFlying()) {
			if(this.isServerWorld()) {
				this.moveRelative(strafe, forward, 0.1F);
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.9D;
				this.motionY *= 0.9D;
				this.motionZ *= 0.9D;
			}
			else {
				super.moveEntityWithHeading(strafe, forward);
			}
		}
		else super.moveEntityWithHeading(strafe, forward);
	}

	@Override
	public boolean isOnLadder() {
		return !isFlying() && super.isOnLadder();
	}

	public int getFlyingHeight() {
		return flyingHeight;
	}

	public void setFlyingHeight(int flyingHeight) {
		this.flyingHeight = flyingHeight;
	}

	public TouhouSpecies getSpecies() {
		return species;
	}

	protected void setSpecies(TouhouSpecies species) {
		this.species = species;
	}

	public boolean isFlying() {
		return getFlyingHeight() > 0;
	}

	public Vector3 pos() {
		return new Vector3(this);
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
		else return super.getMaxFallHeight();
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
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		setFlyingHeight(tag.getByte(NBT_FLYINGHEIGHT));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setByte(NBT_FLYINGHEIGHT, (byte)getFlyingHeight());
	}
}
