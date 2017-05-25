/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.math.DoubleMath;

import io.netty.buffer.ByteBuf;
import net.katsstuff.danmakucore.CoreDataSerializers;
import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.helper.NBTHelper;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.data.LibSubEntities;
import net.katsstuff.danmakucore.misc.LogicalSideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDanmaku extends Entity implements IProjectile, IEntityAdditionalSpawnData {

	public static final double EPSILON = 1E-5;

	private static final String NBT_SHOT_DATA = "shotData";
	private static final String NBT_ANGLE = "angle";
	private static final String NBT_ROTATION = "rotation";
	private static final String NBT_MOVEMENT = "movement";
	private static final String NBT_SOURCE_UUID = "sourceUUID";
	private static final String NBT_USER_UUID = "userUUID";
	private static final String NBT_ROLL = "roll";

	private static final DataParameter<ShotData> SHOT_DATA = EntityDataManager.createKey(EntityDanmaku.class, CoreDataSerializers.SHOTDATA);
	private static final DataParameter<Float> ROLL = EntityDataManager.createKey(EntityDanmaku.class, DataSerializers.FLOAT);

	@LogicalSideOnly(Side.SERVER)
	private EntityLivingBase user;
	@LogicalSideOnly(Side.SERVER)
	private Entity source;
	@LogicalSideOnly(Side.SERVER)
	private Vector3 angle;
	@LogicalSideOnly(Side.SERVER)
	private MovementData movement;
	@LogicalSideOnly(Side.SERVER)
	private RotationData rotation;

	private SubEntity subEntity;

	private boolean frozen = false;

	public EntityDanmaku(World world) {
		super(world);
		isImmuneToFire = true;
		ignoreFrustumCheck = true;
	}

	public EntityDanmaku(World world, EntityLivingBase user, Entity source, ShotData shot) {
		this(world, shot);
		this.user = user;
		this.source = source;

		movement = MovementData.constant(0.4D);
		rotation = RotationData.none();

		setLocationAndAngles(user.posX, user.posY + user.getEyeHeight(), user.posZ, user.rotationYaw, user.rotationPitch);

		posX -= MathHelper.cos((float)Math.toRadians(rotationYaw)) * 0.16F;
		posY -= 0.1D;
		posZ -= MathHelper.sin((float)Math.toRadians(rotationYaw)) * 0.16F;
		setPosition(posX, posY, posZ);

		angle = new Vector3(user.getLookVec());
		resetMotion();
	}

	public EntityDanmaku(World world, ShotData shot, Vector3 pos, Vector3 angle, MovementData movement) {
		this(world, shot);
		this.angle = angle;
		this.movement = movement;
		rotation = RotationData.none();
		setPosition(pos.x(), pos.y(), pos.z());
		resetMotion();
	}

	public EntityDanmaku(World world, @Nullable EntityLivingBase user, @Nullable Entity source, ShotData shot, Vector3 pos, Vector3 angle, float
			roll, MovementData movement, RotationData rotation) {
		this(world, shot, pos, angle, movement);
		this.user = user;
		this.source = source;
		setRoll(roll);
		this.rotation = rotation;
	}

	private EntityDanmaku(World world, ShotData shot) {
		this(world);
		setShotData(shot);
	}

	private EntityDanmaku(EntityDanmaku old) {
		this(old.world, old.user, old.source, old.getShotData(), new Vector3(old), old.angle, old.getRoll(), old.movement, old.rotation);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		getShotData().serializeByteBuf(buf);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		ShotData shot = new ShotData(buf);
		setShotData(shot);
		setSize(shot.sizeX(), shot.sizeY(), shot.sizeZ());
	}

	@SuppressWarnings("unused")
	public EntityDanmaku copy() {
		return new EntityDanmaku(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		double d0 = getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

		if(Double.isNaN(d0)) {
			d0 = 4.0D;
		}

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
		Vector3 vec = new Vector3(x, y, z);
		double length = vec.length();
		x = x / length;
		y = y / length;
		z = z / length;
		x += rand.nextGaussian() * 0.0075D * inaccuracy;
		y += rand.nextGaussian() * 0.0075D * inaccuracy;
		z += rand.nextGaussian() * 0.0075D * inaccuracy;
		x *= velocity;
		y *= velocity;
		z *= velocity;
		motionX = x;
		motionY = y;
		motionZ = z;

		prevRotationYaw = rotationYaw = (float)vec.yaw();
		prevRotationPitch = rotationPitch = (float)vec.pitch();

		if(!world.isRemote) {
			angle = vec.normalize();
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		ShotData shot = getShotData();

		if(subEntity == null) {
			LogHelper.warn("For some reason the danmaku entity is missing it's subEntity. Will try to create a new subEntity");
			setShotData(shot, true);
			if(subEntity == null) {
				LogHelper.error("Failed to create new subEntity. Killing entity");
				setDead(); //We intentionally don't check side here. It's bad, but don't want to deal with bad stuff flying around either
				return;
			}
		}

		if(!frozen) {
			//We do the isRemote check inside to make sure that also the client exits here
			if(ticksExisted > shot.end()) {
				delete();
				return;
			}

			if(user != null && user.isDead) {
				if(!world.isRemote) {
					danmakuFinishBonus();
				}
				return;
			}

			super.onUpdate();
			shot.getForm().onTick(this);
			subEntity.subEntityTick();

			setPosition(posX + motionX, posY + motionY, posZ + motionZ);
		}
	}

	public void accelerate(double currentSpeed) {
		double speedAccel = movement.getSpeedAcceleration();
		double upperSpeedLimit = movement.getUpperSpeedLimit();
		double lowerSpeedLimit = movement.getLowerSpeedLimit();

		if(DoubleMath.fuzzyCompare(currentSpeed, upperSpeedLimit, EPSILON) >= 0 && speedAccel >= 0D) {
			setSpeed(upperSpeedLimit);
		}
		else if(DoubleMath.fuzzyCompare(currentSpeed, lowerSpeedLimit, EPSILON) <= 0 && speedAccel <= 0D) {
			setSpeed(lowerSpeedLimit);
		}
		else {
			addSpeed(speedAccel);

			double newCurrentSpeed = getCurrentSpeed();
			if(DoubleMath.fuzzyCompare(newCurrentSpeed, upperSpeedLimit, EPSILON) > 0) {
				setSpeed(upperSpeedLimit);
			}
			else if(DoubleMath.fuzzyCompare(newCurrentSpeed, lowerSpeedLimit, EPSILON) < 0) {
				setSpeed(lowerSpeedLimit);
			}
		}
	}

	/**
	 * Updates the motion to the current angle.
	 */
	public void setSpeed(double speed) {
		motionX = angle.x() * speed;
		motionY = angle.y() * speed;
		motionZ = angle.z() * speed;
	}

	public void addSpeed(double speed) {
		motionX += angle.x() * speed;
		motionY += angle.y() * speed;
		motionZ += angle.z() * speed;
	}

	@SuppressWarnings("WeakerAccess")
	public void resetMotion() {
		double speedOriginal = getMovementData().getSpeedOriginal();
		motionX = angle.x() * speedOriginal;
		motionY = angle.y() * speedOriginal;
		motionZ = angle.z() * speedOriginal;

		prevRotationYaw = rotationYaw = (float)angle.yaw();
		prevRotationPitch = rotationPitch = (float)angle.pitch();
	}

	@Override
	protected void entityInit() {
		dataManager.register(SHOT_DATA, ShotData.DefaultShotData());
		dataManager.register(ROLL, 0F);
	}

	public ShotData getShotData() {
		return dataManager.get(SHOT_DATA);
	}

	public void setShotData(ShotData shot) {
		setShotData(shot, false);
	}

	public void setShotData(ShotData shot, boolean forceNewSubentity) {
		ShotData oldShot = getShotData();

		boolean first = subEntity == null; //The first time we call this the subentity isn't created yet
		ShotData toUse = first ? shot : subEntity.onShotDataChange(oldShot, oldShot.form().onShotDataChange(oldShot, shot), shot);

		SubEntityType oldSubEntity = getShotData().subEntity();
		dataManager.set(SHOT_DATA, toUse);
		if(toUse.subEntity() != oldSubEntity || first || forceNewSubentity) {
			subEntity = toUse.subEntity().instantiate(world, this);
		}
	}

	public float getRoll() {
		return dataManager.get(ROLL);
	}

	@SuppressWarnings("WeakerAccess")
	public void setRoll(float roll) {
		dataManager.set(ROLL, roll);
	}

	@SuppressWarnings("WeakerAccess")
	public SubEntity setSubEntity(SubEntityType type) {
		setShotData(getShotData().setSubEntity(type));
		return subEntity;
	}

	public Optional<EntityLivingBase> getUser() {
		return Optional.ofNullable(user);
	}

	public Optional<Entity> getSource() {
		return Optional.ofNullable(source);
	}

	public Vector3 getAngle() {
		return angle;
	}

	public void setAngle(Vector3 angle) {
		this.angle = angle;
	}

	public MovementData getMovementData() {
		return movement;
	}

	@SuppressWarnings("unused")
	public void setMovementData(MovementData movement) {
		MovementData old = this.movement;
		this.movement = subEntity.onMovementDataChange(old, getShotData().form().onMovementDataChange(old, movement), movement);
	}

	public RotationData getRotationData() {
		return rotation;
	}

	@SuppressWarnings("unused")
	public void setRotationData(RotationData rotation) {
		RotationData old = this.rotation;
		this.rotation = subEntity.onRotationDataChange(old, getShotData().form().onRotationDataChange(old, rotation), rotation);
	}

	public double getCurrentSpeed() {
		return Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	@SuppressWarnings("WeakerAccess")
	protected void setSize(float width, float height, float length) {
		float f = this.width;
		this.width = width;
		this.height = height;
		ShotData shot = getShotData();
		setShotData(shot.setSizeX(width).setSizeY(height).setSizeZ(length));
		setEntityBoundingBox(new AxisAlignedBB(getEntityBoundingBox().minX, getEntityBoundingBox().minY, getEntityBoundingBox().minZ,
				getEntityBoundingBox().minX + width, getEntityBoundingBox().minY + height, getEntityBoundingBox().minZ + length));

		if(this.width > f && !firstUpdate && !world.isRemote) {
			moveEntity(f - width, 0.0D, f - length);
		}
	}

	@Override
	protected void setSize(float width, float height) {
		setSize(width, height, width);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		if(dataManager != null && ConfigHandler.danmaku.useComplexHitbox) {
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			ShotData shot = getShotData();
			Quat rotation = Quat.eulerToQuat(rotationYaw, rotationPitch, getRoll());
			Vector3 size = new Vector3(shot.sizeX(), shot.sizeY(), shot.sizeZ()).rotate(rotation);
			double xSize = size.x() / 2F;
			double zSize = size.z() / 2F;
			double ySize = size.y();
			if(ySize < 0) {
				this.setEntityBoundingBox(new AxisAlignedBB(x - xSize, y - ySize, z - zSize, x + xSize, y, z + zSize));
			}
			else {
				this.setEntityBoundingBox(new AxisAlignedBB(x - xSize, y, z - zSize, x + xSize, y + ySize, z + zSize));
			}
		}
		else {
			super.setPosition(x, y, z);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbtTag) {
		getUser().ifPresent(living -> nbtTag.setUniqueId(NBT_USER_UUID, living.getUniqueID()));
		getSource().ifPresent(entity -> nbtTag.setUniqueId(NBT_SOURCE_UUID, entity.getUniqueID()));
		NBTHelper.setVector(nbtTag, NBT_ANGLE, angle);

		nbtTag.setTag(NBT_MOVEMENT, movement.serializeNBT());
		nbtTag.setTag(NBT_ROTATION, rotation.serializeNBT());

		ShotData shot = getShotData();
		NBTTagCompound nbtShotData = shot.serializeNBT();
		nbtTag.setTag(NBT_SHOT_DATA, nbtShotData);
		nbtTag.setFloat(NBT_ROLL, getRoll());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbtTag) {
		angle = NBTHelper.getVector(nbtTag, NBT_ANGLE);
		movement = MovementData.fromNBT(nbtTag.getCompoundTag(NBT_MOVEMENT));
		rotation = RotationData.fromNBT(nbtTag.getCompoundTag(NBT_ROTATION));

		NBTTagCompound nbtShotData = nbtTag.getCompoundTag(NBT_SHOT_DATA);
		ShotData shot = new ShotData(nbtShotData);
		setShotData(shot);

		setRoll(nbtTag.getFloat(NBT_ROLL));

		UUID userUUID = nbtTag.getUniqueId(NBT_USER_UUID);
		if(userUUID != null) {
			user = world.getPlayerEntityByUUID(userUUID);

			if(user == null) {
				Entity entity = getEntityByUUID(userUUID);
				if(entity instanceof EntityLivingBase) {
					user = (EntityLivingBase)entity;
				}
			}
		}

		UUID sourceUUID = nbtTag.getUniqueId(NBT_SOURCE_UUID);
		if(sourceUUID != null) {
			source = world.getPlayerEntityByUUID(sourceUUID);

			if(source == null) {
				source = getEntityByUUID(sourceUUID);
			}
		}
	}

	@Nullable
	private Entity getEntityByUUID(UUID uuid) {
		for(Entity entity : world.loadedEntityList) {
			if(uuid.equals(entity.getUniqueID())) return entity;
		}
		return null;
	}

	/**
	 * Side safe way to remove danmaku.
	 */
	public void delete() {
		if(!world.isRemote) {
			setDead();
		}
	}

	/**
	 * Sets the subEntity of this Danmaku to the default.
	 */
	@SuppressWarnings("unused")
	public void subEntityDefault() {
		setSubEntity(LibSubEntities.DEFAULT_TYPE);
	}

	/**
	 * Tests if this shot will be removed because of the end time.
	 */
	@SuppressWarnings("unused")
	public boolean isShotEndTime() {
		return ticksExisted >= getShotData().end();
	}

	public void danmakuFinishBonus() {
		ShotData shot = getShotData();
		Vector3 pos = new Vector3(this);
		//noinspection ConstantConditions
		Optional<EntityLivingBase> target = getUser().flatMap(u -> {
			DamageSource lastDamageSource = u.getLastDamageSource();

			if(lastDamageSource != null) {
				Entity sourceOfDamage = lastDamageSource.getEntity();
				if(sourceOfDamage instanceof EntityLivingBase) {
					return Optional.of((EntityLivingBase)sourceOfDamage);
				}
			}

			return Optional.empty();
		});
		Vector3 angle = target.map(to -> Vector3.angleToEntity(this, to)).orElse(Vector3.Down());

		if(shot.sizeZ() > 1F && shot.sizeZ() / shot.sizeX() > 3) {
			double zPos = 0.0D;
			while(zPos < shot.sizeZ()) {
				Vector3 realPos = pos.offset(angle, zPos);

				world.spawnEntityInWorld(TouhouHelper.createScoreGreen(world, target.orElse(null), realPos, angle));
				zPos += 1D;
			}
			setDead();
		}
		else {
			world.spawnEntityInWorld(TouhouHelper.createScoreGreen(world, target.orElse(null), pos, angle));
			setDead();
		}
	}

	@Override
	public Vec3d getLookVec() {
		return angle.toVec3d();
	}

	@Override
	public float getBrightness(float f) {
		return 1.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float f) {
		return 0xf000f0;
	}

	@SuppressWarnings("unused")
	public SubEntity getSubEntity() {
		return subEntity;
	}

	public Random getRNG() {
		return rand;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}