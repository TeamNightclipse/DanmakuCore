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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

@SuppressWarnings("UnusedParameters")
public abstract class RegistryValueItemStack<T extends IForgeRegistryEntry<T>> extends RegistryValue<T>
		implements Comparable<T>, ITranslatable {

	/**
	 * Called when a itemStack representing this is rightclicked.
	 *
	 * @return If the the action should continue.
	 */
	public abstract boolean onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand);

	@Override
	@ParametersAreNonnullByDefault
	public int compareTo(T other) {
		return getRegistryName().toString().compareToIgnoreCase(other.getRegistryName().toString());
	}

	public abstract ModelResourceLocation getItemModel();
}
