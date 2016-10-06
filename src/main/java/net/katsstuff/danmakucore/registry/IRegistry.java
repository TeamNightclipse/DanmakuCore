/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public interface IRegistry<T extends IForgeRegistryEntry.Impl<T>> {

	default T register(T value) {
		getRegistry().register(value);
		return value;
	}

	default T get(ResourceLocation name) {
		return getRegistry().getObject(name);
	}

	default T get(int id) {
		return getRegistry().getObjectById(id);
	}

	default int getId(T value) {
		return getRegistry().getId(value);
	}

	default List<T> getValues() {
		return getRegistry().getValues();
	}

	FMLControlledNamespacedRegistry<T> getRegistry();
}
