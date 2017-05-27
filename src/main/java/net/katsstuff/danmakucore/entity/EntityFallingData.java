/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.CoreDataSerializers;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.helper.NBTHelper;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFallingData extends Entity {

	public enum DataType {
		SCORE_GREEN,
		SCORE_BLUE,
		POWER,
		BIG_POWER,
		LIFE,
		BOMB
	}

	public static final DataSerializer<DataType> DATA_TYPE_SERIALIZER = new CoreDataSerializers.EnumSerializer<>(DataType.class);

	private static final DataParameter<DataType> DATA_TYPE = EntityDataManager.createKey(EntityFallingData.class, DATA_TYPE_SERIALIZER);

	@Nullable
	private Entity target;
	private Vector3 direction;
	private float amount;

	public EntityFallingData(World worldIn) {
		super(worldIn);
		setSize(0.5F, 0.5F);
	}

	public EntityFallingData(World world, DataType dataType, Vector3 pos, Vector3 direction, @Nullable Entity target, float amount) {
		this(world);
		setDataType(dataType);
		setPositionAndRotation(pos.x(), pos.y(), pos.z(), (float)direction.yaw(), (float)direction.pitch());
		this.target = target;
		this.direction = direction;
		this.amount = amount;
	}

	@Override
	protected void entityInit() {
		dataManager.register(DATA_TYPE, DataType.SCORE_GREEN);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!world.isRemote) {

			Vector3 motion;
			if(target != null) {
				motion = Vector3.directionToEntity(this, target);
			}
			else {
				motion = direction.multiply(0.25);
			}

			motionX = motion.x();
			motionY = motion.y();
			motionZ = motion.z();

			if(!world.isAirBlock(new BlockPos(posX + motionX, posY + motionY, posZ + motionZ))) {
				setDead();
				return;
			}

			setPosition(posX + motionX, posY + motionY, posZ + motionZ);
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if(!world.isRemote) {
			switch(getDataType()) {
				case SCORE_GREEN:
				case SCORE_BLUE:
					TouhouHelper.changeAndSyncPlayerData(data -> data.addScore((int)amount), player);
					break;
				case POWER:
				case BIG_POWER:
					TouhouHelper.changeAndSyncPlayerData(data -> data.addPower(amount), player);
					break;
				case LIFE:
					TouhouHelper.changeAndSyncPlayerData(data -> data.addLives((int)amount), player);
					break;
				case BOMB:
					TouhouHelper.changeAndSyncPlayerData(data -> data.addBombs((int)amount), player);
					break;
				default:
					break;
			}
			this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.75F, 1.3F);
			setDead();
		}
	}

	public DataType getDataType() {
		return dataManager.get(DATA_TYPE);
	}

	public void setDataType(DataType dataType) {
		dataManager.set(DATA_TYPE, dataType);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		UUID target = compound.getUniqueId("target");
		if(target != null) {
			Entity targetEntity = world.getPlayerEntityByUUID(target);
			if(targetEntity == null) {
				NBTHelper.getEntityByUUID(target, world);
			}
			this.target = targetEntity;
		}

		direction = NBTHelper.getVector(compound, "direction");
		amount = compound.getFloat("amount");

		setDataType(DataType.class.getEnumConstants()[compound.getInteger("dataType")]);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		if(target != null) {
			compound.setUniqueId("target", target.getUniqueID());
		}

		NBTHelper.setVector(compound, "direction", direction);
		compound.setFloat("amount", amount);
		compound.setInteger("dataType", getDataType().ordinal());
	}
}
