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
import net.katsstuff.danmakucore.entity.living.ai.pathfinding.FlyMoveHelper;
import net.katsstuff.danmakucore.entity.living.ai.pathfinding.PathNavigateFlyer;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class EntityDanmakuMob extends EntityMob {

	private static final String NBT_FLYINGHEIGHT = "flyingHeight";
	private static final String NBT_PHASE_MANAGER = "phaseManager";

	protected final PhaseManager phaseManager;

	private int flyingHeight;
	private EnumSpecies species;

	public EntityDanmakuMob(World world) {
		super(world);
		moveHelper = new FlyMoveHelper(this);
		setSpecies(EnumSpecies.OTHERS);
		phaseManager = new PhaseManager(this);
	}

	@Override
	protected PathNavigate getNewNavigator(World worldIn) {
		return new PathNavigateFlyer(this, worldIn);
	}

	@Override
	protected void onDeathUpdate() {
		super.onDeathUpdate();

		if(deathTime == 7) {
			DanmakuHelper.explosionEffect2(world, pos(), 1.0F + deathTime * 0.1F);
			DanmakuHelper.chainExplosion(this, 5.0F, 5.0F);
		}

	}

	@Override
	public void onUpdate() {
		if(ConfigHandler.danmaku.danmakuLevel == EnumDanmakuLevel.PEACEFUL) {
			setAttackTarget(null);
		}

		super.onUpdate();
		phaseManager.tick();
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		phaseManager.getCurrentPhase().addTrackingPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		phaseManager.getCurrentPhase().removeTrackingPlayer(player);
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float damage) {
		if(!(damageSource instanceof DamageSourceDanmaku)) {
			damage *= 0.4F;
		}

		return super.attackEntityFrom(damageSource, damage);
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if(isFlying()) {
			if(this.isServerWorld()) {
				this.moveRelative(strafe, forward, 0.1F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
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

	public PhaseManager getPhaseManager() {
		return phaseManager;
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

	/**
	 * How many power entities to spawn when this entity dies.
	 */
	public int powerSpawns() {
		return rand.nextInt(3);
	}

	/**
	 * How many point entities to spawn when this entity dies.
	 */
	public int pointSpawns() {
		return rand.nextInt(4);
	}

	/**
	 * Loot that is dropped every phase.
	 */
	protected void dropPhaseLoot(DamageSource source) {
		Vector3 pos = pos();
		Vector3 angle;
		if(source.getEntity() != null) {
			angle = Vector3.angleToEntity(this, source.getEntity());
		}
		else {
			angle = Vector3.Down();
		}

		int powerSpawns = powerSpawns();
		for(int i = 1; i < powerSpawns; i++) {
			world.spawnEntityInWorld(TouhouHelper.createPower(world, pos, angle));
		}

		int pointSpawns = pointSpawns();
		for(int i = 1; i < pointSpawns; i++) {
			world.spawnEntityInWorld(TouhouHelper.createScoreBlue(world, null, pos, angle));
		}
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		dropPhaseLoot(source);
		phaseManager.getCurrentPhase().dropLoot(source);
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
	}
}
