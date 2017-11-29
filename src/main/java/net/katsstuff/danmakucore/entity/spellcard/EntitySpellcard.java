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

import net.katsstuff.danmakucore.entity.spellcard.spellcardbar.SpellcardInfoServer;
import net.katsstuff.danmakucore.helper.DanmakuHelper;
import net.katsstuff.danmakucore.helper.NBTHelper;
import net.katsstuff.danmakucore.misc.LogicalSideOnly;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class EntitySpellcard extends Entity {

	private static final DataParameter<Integer> SPELLCARD_ID = EntityDataManager.createKey(EntitySpellcard.class, DataSerializers.VARINT);

	private static final String NBT_SPELLCARD_TYPE = "spellcardType";
	private static final String NBT_SPELLCARD_DATA = "spellcardData";
	private static final String NBT_USER = "user";

	private boolean sendNamePacket = false;
	private SpellcardInfoServer spellcardInfo;

	@LogicalSideOnly(Side.SERVER)
	private EntityLivingBase user;
	@LogicalSideOnly(Side.SERVER)
	private SpellcardEntity spellCard;

	public EntitySpellcard(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(0.4F, 0.6F);
	}

	public EntitySpellcard(EntityLivingBase user, @Nullable EntityLivingBase target, Spellcard type, boolean sendInfoPacket) {
		this(user.world);

		setPosition(user.posX, user.posY + user.getEyeHeight(), user.posZ);
		setRotation(user.rotationYaw, user.rotationPitch);
		this.user = user;
		spellCard = type.instantiate(this, target);
		setSpellcardId(DanmakuRegistry.getId(Spellcard.class, type));
		this.sendNamePacket = sendInfoPacket;

		if(sendInfoPacket) {
			spellcardInfo = new SpellcardInfoServer(spellCard.getName());
		}
	}

	@Override
	public void onUpdate() {
		if(!world.isRemote && (spellCard == null || ticksExisted >= spellCard.type.getEndTime() || user == null || user.isDead)) {
			setDead();
			return;
		}

		super.onUpdate();

		if(spellcardInfo != null) {
			spellcardInfo.tick();
		}

		if(!world.isRemote) {
			spellCard.onUpdate();
			if(user instanceof EntityPlayer && ticksExisted < spellCard.type.getRemoveTime()) {
				DanmakuHelper.danmakuRemove(user, 40.0F, DanmakuHelper.DanmakuRemoveMode.OTHER, true);
			}
		}
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		if(sendNamePacket) {
			spellcardInfo.addPlayer(player);
		}
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		if(sendNamePacket) {
			spellcardInfo.removePlayer(player);
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

	public void updateName() {
		if(spellcardInfo != null) {
			spellcardInfo.setName(spellCard.getName());
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		UUID userUuid = NBTUtil.getUUIDFromTag(tag.getCompoundTag(NBT_USER));

		user = world.getPlayerEntityByUUID(userUuid);
		if(user == null) {
			Optional<Entity> optUser = NBTHelper.getEntityByUUID(userUuid, world);
			if(optUser.isPresent()) {
				Entity foundUser = optUser.get();
				if(foundUser instanceof EntityLivingBase) {
					user = (EntityLivingBase)foundUser;
				}
			}
		}

		if(user != null) {
			Spellcard type = DanmakuRegistry.SPELLCARD.getValue(new ResourceLocation(tag.getString(NBT_SPELLCARD_TYPE)));

			if(type == null) {
				setDead();
			}
			else {
				setSpellcardId(DanmakuRegistry.getId(Spellcard.class, type));
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
		tag.setString(NBT_SPELLCARD_TYPE, spellCard.type.getFullNameString());
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
	public float getBrightness() {
		return 0.5F;
	}

	@LogicalSideOnly(Side.SERVER)
	public EntityLivingBase getUser() {
		return user;
	}

	@LogicalSideOnly(Side.SERVER)
	public SpellcardEntity getSpellCard() {
		return spellCard;
	}

	public Spellcard getSpellcardType() {
		return DanmakuRegistry.getObjById(Spellcard.class, getSpellcardId());
	}
}
