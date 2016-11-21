/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.entity.danmaku.DamageSourceDanmaku;
import net.katsstuff.danmakucore.helper.TouhouHelper;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class DanmakuCoreDataHandler {

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		TouhouHelper.getDanmakuCoreData(event.player).ifPresent(data -> {
			if(event.player instanceof EntityPlayerMP) {
				data.syncTo((EntityPlayerMP)event.player, event.player);
			}
		});
	}

	@SubscribeEvent
	public void attachPlayer(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(LibMod.MODID, "DanmakuCoreData"), new DanmakuCoreDataProvider());
		}
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if(!living.worldObj.isRemote && event.getSource() instanceof DamageSourceDanmaku && living instanceof EntityPlayer
				&& TouhouHelper.getDanmakuCoreData(living).map(IDanmakuCoreData::getLives).orElse(0) >= 1) {
			EntityPlayer player = (EntityPlayer)living;
			TouhouHelper.changeAndSyncPlayerData(IDanmakuCoreData::removeLife, player);
			player.isDead = false;
			player.setHealth(player.getMaxHealth());
			player.hurtResistantTime = 50;
			//TODO: Fancy sound and animation
			event.setCanceled(true);
		}
	}

	public static class DanmakuCoreDataProvider implements ICapabilitySerializable<NBTTagCompound> {

		@CapabilityInject(IDanmakuCoreData.class)
		public static Capability<IDanmakuCoreData> CORE_DATA;


		private final IDanmakuCoreData data = new BoundedDanmakuCoreData();

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == CORE_DATA;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
			return capability == CORE_DATA ? CORE_DATA.cast(data) : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound)CORE_DATA.writeNBT(data, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			CORE_DATA.readNBT(data, null, nbt);
		}
	}
}
