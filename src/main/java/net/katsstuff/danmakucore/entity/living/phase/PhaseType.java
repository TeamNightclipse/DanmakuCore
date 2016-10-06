/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.phase;

import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.IRegistryValue;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public abstract class PhaseType extends IForgeRegistryEntry.Impl<PhaseType> implements IRegistryValue<PhaseType> {

	public abstract Phase instantiate(PhaseManager phaseManager);

	@Override
	public FMLControlledNamespacedRegistry<PhaseType> getRegistry() {
		return DanmakuRegistry.INSTANCE.phase.getRegistry();
	}

	@Override
	public PhaseType getObject() {
		return this;
	}
}
