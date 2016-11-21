/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.helper.NBTHelper;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntitySpellcard extends Entity {

	private static final DataParameter<Integer> SPELLCARD_ID = EntityDataManager.createKey(EntitySpellcard.class, DataSerializers.VARINT);

	private static final String NBT_SPELLCARD_TYPE = "spellcardType";
	private static final String NBT_SPELLCARD_DATA = "spellcardData";
	private static final String NBT_USER = "user";

	private EntityLivingBase user;
	private SpellcardEntity spellCard;

	public EntitySpellcard(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(0.4F, 0.6F);
	}

	public EntitySpellcard(EntityLivingBase user, @Nullable EntityLivingBase target, Spellcard type) {
		this(user.worldObj);

		setPosition(user.posX, user.posY + user.getEyeHeight(), user.posZ);
		setRotation(user.rotationYaw, user.rotationPitch);
		this.user = user;
		spellCard = type.instantiate(this, target);
		setSpellcardId(DanmakuRegistry.SPELLCARD.getId(type));
	}

	@Override
	public void onUpdate() {
		if(!worldObj.isRemote && (spellCard == null || ticksExisted >= spellCard.type.getEndTime() || user == null || user.isDead)) {
			setDead();
			return;
		}

		super.onUpdate();

		if(!worldObj.isRemote) {
			spellCard.onUpdate();
			if(user instanceof EntityPlayer && ticksExisted < spellCard.type.getRemoveTime()) {
				DanmakuHelper.danmakuRemove(user, 40.0F, DanmakuHelper.DanmakuRemoveMode.OTHER, true);
			}
		}
	}

	@Override
	protected void entityInit() {
		dataManager.register(SPELLCARD_ID, 0);
	}

	private void setSpellcardId(int number) {
		dataManager.set(SPELLCARD_ID, number);
	}

	public int getSpellcardId() {
		return dataManager.get(SPELLCARD_ID);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		EntityLivingBase user;
		UUID userUuid = NBTUtil.getUUIDFromTag(tag.getCompoundTag(NBT_USER));

		user = worldObj.getPlayerEntityByUUID(userUuid);
		if(user == null) {
			Optional<Entity> optUser = NBTHelper.getEntityByUUID(userUuid, worldObj);
			if(optUser.isPresent()) {
				Entity foundUser = optUser.get();
				if(foundUser instanceof EntityLivingBase) {
					user = (EntityLivingBase)foundUser;
				}
			}
		}

		if(user != null) {
			this.user = user;
			Spellcard type = DanmakuRegistry.SPELLCARD.getValue(new ResourceLocation(tag.getString(NBT_SPELLCARD_TYPE)));

			if(type == null) {
				setDead();
			}
			else {
				setSpellcardId(DanmakuRegistry.SPELLCARD.getId(type));
				spellCard = type.instantiate(this, user);
				spellCard.deserializeNBT(tag.getCompoundTag(NBT_SPELLCARD_DATA));
			}
		}
		else {
			setDead();
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setString(NBT_SPELLCARD_TYPE, spellCard.type.getFullName().toString());
		tag.setTag(NBT_SPELLCARD_DATA, spellCard.serializeNBT());
		tag.setUniqueId(NBT_USER, user.getUniqueID());
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public float getBrightness(float par1) {
		return 0.5F;
	}

	public EntityLivingBase getUser() {
		return user;
	}

	@SuppressWarnings("unused")
	public SpellcardEntity getSpellCard() {
		return spellCard;
	}
}
