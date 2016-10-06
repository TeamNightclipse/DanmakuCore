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
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;

@SuppressWarnings("WeakerAccess")
public class Registry<T extends IForgeRegistryEntry.Impl<T> & IRegistryValue<T>> implements IRegistry<T> {

	public final ResourceLocation defaultLocation;
	public final FMLControlledNamespacedRegistry<T> registry;

	public Registry(ResourceLocation name, Class<T> type, ResourceLocation defaultKey, int minId, int maxId, boolean hasDelegates,
			IForgeRegistry.AddCallback<T> addCallback, IForgeRegistry.ClearCallback<T> clearCallback,
			IForgeRegistry.CreateCallback<T> createCallback, IForgeRegistry.SubstitutionCallback<T> substitutionCallback) {
		this.defaultLocation = defaultKey;
		registry = PersistentRegistryManager.createRegistry(name, type, defaultKey, minId, maxId, hasDelegates, addCallback, clearCallback,
				createCallback, substitutionCallback);
	}

	public Registry(ResourceLocation name, Class<T> type, ResourceLocation defaultKey, int minId, int maxId, boolean hasDelegates) {
		this(name, type, defaultKey, minId, maxId, hasDelegates, null, null, null, null);
	}

	public Registry(ResourceLocation name, Class<T> type, ResourceLocation defaultKey) {
		this(name, type, defaultKey, 0, Short.MAX_VALUE, true);
	}

	@Override
	public FMLControlledNamespacedRegistry<T> getRegistry() {
		return registry;
	}
}
