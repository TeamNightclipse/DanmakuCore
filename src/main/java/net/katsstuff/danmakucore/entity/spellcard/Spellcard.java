/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.entity.living.boss.EnumTouhouCharacters;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.IRegistryValueItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

@SuppressWarnings("UnusedParameters")
public abstract class Spellcard extends IForgeRegistryEntry.Impl<Spellcard> implements IRegistryValueItemStack<Spellcard> {

	public abstract SpellcardEntity instantiate(EntitySpellcard card, @Nullable EntityLivingBase target);

	/**
	 * @return The needed XP Levels to execute the spellcard.
	 */
	public abstract int getNeededLevel();

	/**
	 * @return The remove time. How long the spellcard will remove danmaku for.
	 */
	public abstract int getRemoveTime();

	/**
	 * @return The end time. How long the spellcard will physically exist and do stuff. Can also be
	 * though of as cooldown.
	 */
	public abstract int getEndTime();

	/**
	 * @return The Touhou character that uses this spellcard in the games.
	 */
	public abstract EnumTouhouCharacters getOriginalUser();

	/**
	 * Called when a spellcard itemStack is rightclicked.
	 *
	 * @return If the spellcard should be declared.
	 */
	@Override
	public boolean onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		return true;
	}

	/**
	 * Called before declaring a spellcard of this type.
	 *
	 * @return If the spellcard is allowed to be declared.
	 */
	public boolean onDeclare(EntityLivingBase user, @Nullable EntityLivingBase target, boolean firstAttack) {
		return true;
	}

	@Override
	public FMLControlledNamespacedRegistry<Spellcard> getRegistry() {
		return DanmakuRegistry.INSTANCE.spellcard.getRegistry();
	}

	@Override
	public Spellcard getObject() {
		return this;
	}

	@Override
	public int compareTo(Spellcard other) {
		return getOriginalUser().getName().compareToIgnoreCase(other.getOriginalUser().getName());
	}

	@Override
	public String getUnlocalizedName() {
		return "spellcard";
	}
}
