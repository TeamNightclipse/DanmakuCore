/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public interface IRegistryValue<T extends IForgeRegistryEntry.Impl<T> & IRegistryValue<T>> {

	/**
	 * Get the registry for this value.
	 */
	FMLControlledNamespacedRegistry<T> getRegistry();

	/**
	 * The full name as of this value. Both modId and name.
	 */
	default ResourceLocation getFullName() {
		return getRegistry().getKey(getObject());
	}

	/**
	 * Get the mod id for this value.
	 */
	default String getModId() {
		return getFullName().getResourceDomain();
	}

	/**
	 * Get the short name for this value.
	 */
	default String getName() {
		return getFullName().getResourcePath();
	}

	/**
	 * Returns this object, so that it can be used for the default implementation.
	 */
	T getObject();

}
