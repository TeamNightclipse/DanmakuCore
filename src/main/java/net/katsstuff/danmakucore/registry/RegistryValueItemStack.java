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
import net.minecraftforge.registries.IForgeRegistryEntry;

@SuppressWarnings("UnusedParameters")
public abstract class RegistryValueItemStack<T extends IForgeRegistryEntry<T>> extends RegistryValue<T> implements Comparable<T>, ITranslatable {

	/**
	 * Called when a itemStack representing this is rightclicked.
	 *
	 * @return If the the action should continue.
	 */
	public abstract boolean onRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand);

	@Override
	@ParametersAreNonnullByDefault
	public int compareTo(T other) {
		return getFullNameString().compareToIgnoreCase(other.getRegistryName().toString());
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof RegistryValueItemStack)) {
			return false;
		}

		if(obj == this) {
			return true;
		}

		return getFullNameString().equalsIgnoreCase(((RegistryValueItemStack)obj).getFullNameString());
	}

	@Override
	public int hashCode() {
		return getFullName().hashCode();
	}

	public abstract ModelResourceLocation getItemModel();
}
