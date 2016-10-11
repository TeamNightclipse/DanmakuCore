/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.spellcard.EntitySpellcard;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.capability.CapabilityDanmakuCoreData;
import net.katsstuff.danmakucore.capability.IDanmakuCoreData;
import net.katsstuff.danmakucore.item.DanmakuCoreItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class TouhouHelper {

	public static boolean declareSpellcardPlayer(EntityPlayer player, Spellcard spellcard, boolean firstAttack, boolean simulate) {
		World world = player.worldObj;

		//Only allow spellcard if use user isn't already using a spellcard
		@SuppressWarnings("ConstantConditions")
		List<EntitySpellcard> spellcardList = world.getEntitiesWithinAABB(EntitySpellcard.class, player.getEntityBoundingBox().expandXyz(32D),
				entitySpellcard -> player == entitySpellcard.getUser());
		if(!spellcardList.isEmpty()) return false;

		//Get the target
		Optional<Entity> optTarget = Vector3.getEntityLookedAt(player,
				targetEntity -> targetEntity instanceof EntityLivingBase && !(targetEntity instanceof EntityAgeable));

		if(!optTarget.isPresent()) return false;
		EntityLivingBase target = (EntityLivingBase)optTarget.get();

		//Checks if the user has levels + bombs to do spellcard and removes them if not simulated
		int neededLevels = spellcard.getNeededLevel();

		if(!player.capabilities.isCreativeMode) {

			int bombs = 0;

			for(ItemStack stack : player.inventory.mainInventory) {
				if(stack != null && stack.getItem() == DanmakuCoreItem.bombItem) {
					bombs += stack.stackSize;
				}
			}
			if(player.experienceLevel + bombs < neededLevels) return false;

			if(!simulate) {
				int clearedLevels = neededLevels - bombs;
				clearedLevels = clearedLevels < 0 ? 0 : clearedLevels;
				int clearedBombs = neededLevels - clearedLevels;
				player.inventory.clearMatchingItems(DanmakuCoreItem.bombItem, -1, clearedBombs, null);
				player.addExperienceLevel(-clearedLevels);
			}
		}

		if(!simulate) {
			declareSpellcard(player, target, spellcard, firstAttack);
		}
		return true;
	}

	public static void declareSpellcard(EntityLivingBase user, @Nullable EntityLivingBase target, Spellcard spellCard, boolean firstAttack) {
		if(!spellCard.onDeclare(user, target, firstAttack)) return;

		World world = user.worldObj;
		if(!world.isRemote) {
			EntitySpellcard entitySpellCard = new EntitySpellcard(user, target, spellCard);
			entitySpellCard.setPosition(user.posX, user.posY, user.posZ);
			world.spawnEntityInWorld(entitySpellCard);
		}

		//Does chat
		if(!world.isRemote && user instanceof EntityPlayer) {
			user.addChatMessage(new TextComponentTranslation(spellCard.getUnlocalizedName()));
		}

		if(firstAttack) {
			if(!world.isRemote && target instanceof EntityPlayer) {
				target.addChatMessage(new TextComponentTranslation(spellCard.getUnlocalizedName()));
			}

			DanmakuHelper.danmakuRemove(user, 40.0F, DanmakuHelper.DanmakuRemoveMode.ENEMY, true);
		}
	}

	public static Optional<IDanmakuCoreData> getDanmakuCoreData(ICapabilityProvider provider) {
		if(provider.hasCapability(CapabilityDanmakuCoreData.DANMAKUCORE_DATA_CAPABILITY, null)) {
			return Optional.of(provider.getCapability(CapabilityDanmakuCoreData.DANMAKUCORE_DATA_CAPABILITY, null));
		}
		else return Optional.empty();
	}

	public static void changeAndSyncEntityData(Consumer<IDanmakuCoreData> dataRunnable, Entity target, double radius) {
		getDanmakuCoreData(target).ifPresent(data -> {
			dataRunnable.accept(data);

			NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(target.dimension, target.posX, target.posY, target.posZ, radius);
			data.syncToClose(point, target);
		});
	}

	public static void addPowerEntitySync(Entity entity, float power, double radius) {
		changeAndSyncEntityData(data -> data.addPower(power), entity, radius);
	}

	public static void setPowerEntitySync(Entity entity, float power, double radius) {
		changeAndSyncEntityData(data -> data.setPower(power), entity, radius);
	}

	public static void addScoreEntitySync(Entity entity, int score, double radius) {
		changeAndSyncEntityData(data -> data.addScore(score), entity, radius);
	}

	public static void setScoreEntitySync(Entity entity, int score, double radius) {
		changeAndSyncEntityData(data -> data.setScore(score), entity, radius);
	}

	public static void changeAndSyncPlayerData(Consumer<IDanmakuCoreData> dataRunnable, EntityPlayer player) {
		getDanmakuCoreData(player).ifPresent(data -> {
			dataRunnable.accept(data);

			if(player instanceof EntityPlayerMP) {
				data.syncTo((EntityPlayerMP)player, player);
			}
		});
	}

	public static void addPowerPlayerSync(EntityPlayer player, float power) {
		changeAndSyncPlayerData(data -> data.addPower(power), player);
	}

	public static void setPowerPlayerSync(EntityPlayer player, float power) {
		changeAndSyncPlayerData(data -> data.setPower(power), player);
	}

	public static void addScorePlayerSync(EntityPlayer player, int score) {
		changeAndSyncPlayerData(data -> data.addScore(score), player);
	}

	public static void setScorePlayerSync(EntityPlayer player, int score) {
		changeAndSyncPlayerData(data -> data.setScore(score), player);
	}
}
