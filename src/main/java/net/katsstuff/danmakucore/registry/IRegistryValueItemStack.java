/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry;

import javax.annotation.ParametersAreNonnullByDefault;

import net.katsstuff.danmakucore.misc.ITranslatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

@SuppressWarnings("UnusedParameters")
public interface IRegistryValueItemStack<T extends IForgeRegistryEntry.Impl<T> & IRegistryValueItemStack<T>> extends IRegistryValue<T>, Comparable<T>,
		ITranslatable {

	/**
	 * Called when a itemStack representing this is rightclicked.
	 *
	 * @return If the the action should continue.
	 */
	boolean onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand);

	@Override
	@ParametersAreNonnullByDefault
	default int compareTo(T other) {
		return getFullName().toString().compareToIgnoreCase(other.getFullName().toString());
	}
}
