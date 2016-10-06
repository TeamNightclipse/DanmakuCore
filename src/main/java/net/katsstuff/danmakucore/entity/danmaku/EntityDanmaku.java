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
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.katsstuff.danmakucore.CoreDataSerializers;
import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.MutableVector3;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.lib.data.LibSubEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDanmaku extends Entity implements IProjectile, IEntityAdditionalSpawnData {

	private static final String NBT_SHOT_DATA = "shotData";
	private static final String NBT_ANGLE_Z = "angleZ";
	private static final String NBT_ANGLE_Y = "angleY";
	private static final String NBT_ANGLE_X = "angleX";
	private static final String NBT_ROTATION = "rotation";
	private static final String NBT_MOVEMENT = "movement";
	private static final String NBT_SOURCE_UUID = "sourceUUID";
	private static final String NBT_USER_UUID = "userUUID";
	public static final String NBT_SPEED = "speed";

	private static final DataParameter<ShotData> SHOT_DATA = EntityDataManager.createKey(EntityDanmaku.class, CoreDataSerializers.SHOTDATA);
	private static final DataParameter<Float> ROLL = EntityDataManager.createKey(EntityDanmaku.class, DataSerializers.FLOAT);

	private EntityLivingBase user;
	private Entity source;
	private MutableVector3 angle;
	private MovementData movement;
	private RotationData rotation;
	private SubEntity subEntity;

	public EntityDanmaku(World world) {
		super(world);
		isImmuneToFire = true;
		ignoreFrustumCheck = true;
	}

	//FIXME: Where does movement and rotation data get set here?
	public EntityDanmaku(World world, EntityLivingBase user, Entity source, ShotData shot) {
		this(world, shot);
		this.user = user;
		this.source = source;

		setLocationAndAngles(user.posX, user.posY + user.getEyeHeight(), user.posZ, user.rotationYaw, user.rotationPitch);

		//TODO: What does this do? Isn't setThrowableHeading enough?
		posX -= MathHelper.cos((float)Math.toRadians(rotationYaw)) * 0.16F;
		posY -= 0.1D;
		posZ -= MathHelper.sin((float)Math.toRadians(rotationYaw)) * 0.16F;
		setPosition(posX, posY, posZ);

		angle = new MutableVector3(user.getLookVec());
		setThrowableHeading(angle.x(), angle.y(), angle.z(), 0.4F, 0.0F);
	}

	//FIXME: Where does rotation data get set here?
	public EntityDanmaku(World world, ShotData shot, Vector3 pos, Vector3 angle, MovementData movement) {
		this(world, shot);
		this.angle = angle.asMutable();
		this.movement = movement;
		setPosition(pos.x(), pos.y(), pos.z());
		setThrowableHeading(angle.x(), angle.y(), angle.z(), (float)movement.getSpeedOriginal(), 0.0F);
	}

	public EntityDanmaku(World world, @Nullable EntityLivingBase user, @Nullable Entity source, ShotData shot, Vector3 pos, Vector3 angle, float roll,
			MovementData movement, RotationData rotation) {
		this(world, shot, pos, angle, movement);
		this.user = user;
		this.source = source;
		setRoll(roll);
		this.rotation = rotation;
	}

	private EntityDanmaku(World world, ShotData shot) {
		super(world);
		setShotData(shot);
	}

	private EntityDanmaku(EntityDanmaku old) {
		this(old.worldObj, old.user, old.source, old.getShotData(), new Vector3(old), old.angle.asImmutable(), old.getRoll(), old.movement, old.rotation);
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		getShotData().serializeByteBuf(buf);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		ShotData shot = new ShotData(buf);
		setSubEntity(shot.subEntity());
		setSize(shot.sizeX(), shot.sizeY(), shot.sizeZ());
	}

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
		float length = MathHelper.sqrt_double(x * x + y * y + z * z);
		x = x / length;
		y = y / length;
		z = z / length;
		x = x + rand.nextGaussian() * 0.0075D * inaccuracy;
		y = y + rand.nextGaussian() * 0.0075D * inaccuracy;
		z = z + rand.nextGaussian() * 0.0075D * inaccuracy;
		x = x * velocity;
		y = y * velocity;
		z = z * velocity;
		motionX = x;
		motionY = y;
		motionZ = z;

		Vector3 vec = new Vector3(x, y, z);

		prevRotationYaw = rotationYaw = (float)vec.yaw();
		prevRotationPitch = rotationPitch = (float)vec.pitch();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		ShotData shot = getShotData();

		if(!worldObj.isRemote && ticksExisted > shot.end()) {
			setDead();
			return;
		}

		if(subEntity == null) {
			LogHelper.error("For some reason the danmaku entity is missing it's subEntity. Will try to create a new subEntity");
			setSubEntity(getShotData().getSubEntity());
			if(subEntity == null) {
				LogHelper.error("Failed to create new subEntity. Killing entity");
				setDead(); //We intentionally don't check side here. It's bad, but don't want to deal with bad stuff flying around either
				return;
			}
		}

		if(!worldObj.isRemote && user != null && user.isDead) {
			danmakuFinishBonus();
			return;
		}

		int delay = shot.delay();
		if(delay > 0) {
			ticksExisted = 0;
			shot = shot.setDelay(delay - 1);
			motionX = 0;
			motionY = 0;
			motionZ = 0;

			//Do a new check to see if the shot is still delayed, and if it isn't. Start it's movement
			if(!worldObj.isRemote && shot.delay() <= 0) {
				setThrowableHeading(angle.x(), angle.y(), angle.z(), (float)movement.getSpeedOriginal(), 0F);
			}
			setShotData(shot);
		}
		else {
			super.onUpdate();
			shot.getForm().onTick(this);
			subEntity.subEntityTick();
		}

		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		setPosition(posX, posY, posZ);
	}

	//TODO: This method calculates the speed(expensive method because of square root) 1-2 times per call, any better way?
	public void accelerate() {
		if(!worldObj.isRemote) {
			double speedAccel = movement.getSpeedAcceleration();
			double speedLimit = movement.getSpeedLimit();
			if(speedAccel > 0D) {
				if(getCurrentSpeed() < speedLimit) {
					motionX += angle.x() * speedAccel;
					motionY += angle.y() * speedAccel;
					motionZ += angle.z() * speedAccel;
				}

				//We check if we reached the speed limit. Not an if else check as we might have added speed just previously.
				if(getCurrentSpeed() > speedLimit) {
					motionX = angle.x() * speedLimit;
					motionY = angle.y() * speedLimit;
					motionZ = angle.z() * speedLimit;
				}
			}
			//TODO: Rewrite this so that speedLimit can work as both top and bottom speed. In general just rewrite this
			else if(speedAccel < 0D && getCurrentSpeed() > speedLimit) {
				if(Math.abs(speedAccel) > getCurrentSpeed() - speedLimit) {
					motionX = angle.x() * speedLimit;
					motionY = angle.y() * speedLimit;
					motionZ = angle.z() * speedLimit;
				}
				else {
					//This is really subtracting because the acceleration is negative
					motionX += angle.x() * speedAccel;
					motionY += angle.y() * speedAccel;
					motionZ += angle.z() * speedAccel;
				}
			}
		}
	}

	/**
	 * Updates the motion to the current angle.
	 */
	public void updateMotion() {
		if(!worldObj.isRemote) {
			double speed = getCurrentSpeed();
			motionX = angle.x() * speed;
			motionY = angle.y() * speed;
			motionZ = angle.z() * speed;
		}
	}

	@Override
	protected void entityInit() {
		dataManager.register(SHOT_DATA, ShotData.DefaultShotData());
		dataManager.register(ROLL, 0F);
	}

	/**
	 * Get the {@link ShotData} for this danmaku. Although the returned
	 * ShotData is mutable for convenience sake. If you change anything it is,
	 * you still need to use {@link EntityDanmaku#setShotData(ShotData)}.
	 */
	public ShotData getShotData() {
		return dataManager.get(SHOT_DATA);
	}

	private void setShotData(ShotData shot) {
		SubEntityType oldSubEntity = getShotData().subEntity();
		dataManager.set(SHOT_DATA, shot);
		if(shot.subEntity() != oldSubEntity || subEntity == null) {
			subEntity = shot.subEntity().instantiate(worldObj, this);
		}
	}

	public float getRoll() {
		return dataManager.get(ROLL);
	}

	public void setRoll(float roll) {
		dataManager.set(ROLL, roll);
	}

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
		return angle.asImmutable();
	}

	public void setAngle(Vector3 angle) {
		this.angle = angle.asMutable();
	}

	public MovementData getMovementData() {
		return movement;
	}

	public void setMovementData(MovementData movement) {
		this.movement = movement;
	}

	public RotationData getRotationData() {
		return rotation;
	}

	public void setRotationData(RotationData rotation) {
		this.rotation = rotation;
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

		if(this.width > f && !firstUpdate && !worldObj.isRemote) {
			moveEntity(f - width, 0.0D, f - length);
		}
	}

	@Override
	protected void setSize(float width, float height) {
		setSize(width, height, width);
	}

	//TODO: Outdated
	@Override
	public void writeEntityToNBT(NBTTagCompound nbtTag) {
		getUser().ifPresent(living -> nbtTag.setUniqueId(NBT_USER_UUID, living.getUniqueID()));
		getSource().ifPresent(entity -> nbtTag.setUniqueId(NBT_SOURCE_UUID, entity.getUniqueID()));
		nbtTag.setDouble(NBT_ANGLE_X, angle.x());
		nbtTag.setDouble(NBT_ANGLE_Y, angle.y());
		nbtTag.setDouble(NBT_ANGLE_Z, angle.z());

		nbtTag.setTag(NBT_MOVEMENT, movement.serializeNBT());
		nbtTag.setTag(NBT_ROTATION, rotation.serializeNBT());

		ShotData shot = getShotData();
		NBTTagCompound nbtShotData = shot.serializeNBT();
		nbtTag.setTag(NBT_SHOT_DATA, nbtShotData);
	}

	//TODO: Outdated
	@Override
	public void readEntityFromNBT(NBTTagCompound nbtTag) {
		angle = new MutableVector3(nbtTag.getDouble(NBT_ANGLE_X), nbtTag.getDouble(NBT_ANGLE_Y), nbtTag.getDouble(NBT_ANGLE_Z));
		movement = MovementData.fromNBT(nbtTag.getCompoundTag(NBT_MOVEMENT));
		rotation = RotationData.fromNBT(nbtTag.getCompoundTag(NBT_ROTATION));

		NBTTagCompound nbtShotData = nbtTag.getCompoundTag(NBT_SHOT_DATA);
		ShotData shot = new ShotData(nbtShotData);
		setShotData(shot);

		UUID userUUID = nbtTag.getUniqueId(NBT_USER_UUID);
		if(userUUID != null) {
			user = worldObj.getPlayerEntityByUUID(userUUID);

			if(user == null) {
				Entity entity = getEntityByUUID(userUUID);
				if(entity instanceof EntityLivingBase) {
					user = (EntityLivingBase)entity;
				}
			}
		}

		UUID sourceUUID = nbtTag.getUniqueId(NBT_SOURCE_UUID);
		if(sourceUUID != null) {
			source = worldObj.getPlayerEntityByUUID(sourceUUID);

			if(source == null) {
				source = getEntityByUUID(sourceUUID);
			}
		}
	}

	@Nullable
	private Entity getEntityByUUID(UUID uuid) {
		for(Entity entity : worldObj.loadedEntityList) {
			if(uuid.equals(entity.getUniqueID())) return entity;
		}
		return null;
	}

	/**
	 * Side safe way to remove danmaku.
	 */
	public void delete() {
		if(!worldObj.isRemote) {
			setDead();
		}
	}

	/**
	 * Sets the subEntity of this Danmaku to the default.
	 */
	public void subEntityDefault() {
		setSubEntity(LibSubEntities.DEFAULT_TYPE);
	}

	/**
	 * Tests if this shot will be removed because of the end time.
	 */
	public boolean isShotEndTime() {
		return ticksExisted >= getShotData().end();
	}

	public void danmakuFinishBonus() {
		if(!worldObj.isRemote) {
			ShotData shot = getShotData();
			if(shot.sizeZ() * 1.5 < shot.sizeX()) {
				double zPos = 0.0D;
				while(zPos < shot.sizeZ()) {
					//MutableVector3 position = angle.copyObj().multiplyMutable(zPos).addMutable(posX, posY, posZ);
					//TODO: Give score here
					zPos += 1.5D;
				}
				setDead();
			}
			else {
				//TODO: Give score here
				setDead();
			}
		}
	}

	@Override
	public Vec3d getLookVec() {
		return angle.normalize().toVec3d();
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

	public SubEntity getSubEntity() {
		return subEntity;
	}
}