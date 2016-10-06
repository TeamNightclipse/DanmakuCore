/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.subentity;

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.misc.ITranslatable;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.katsstuff.danmakucore.registry.IRegistryValue;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public abstract class SubEntityType extends IForgeRegistryEntry.Impl<SubEntityType> implements IRegistryValue<SubEntityType>, ITranslatable {

	public abstract SubEntity instantiate(World world, EntityDanmaku entityDanmaku);

	@Override
	public FMLControlledNamespacedRegistry<SubEntityType> getRegistry() {
		return DanmakuRegistry.INSTANCE.subEntity.getRegistry();
	}

	@Override
	public SubEntityType getObject() {
		return this;
	}

	@Override
	public String getUnlocalizedName() {
		return "subentity";
	}
}
