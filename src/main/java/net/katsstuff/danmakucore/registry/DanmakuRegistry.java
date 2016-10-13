/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.lib.LibDanmakuVariantName;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.katsstuff.danmakucore.lib.LibRegistryName;
import net.katsstuff.danmakucore.lib.LibSubEntityName;
import net.katsstuff.danmakucore.misc.IInitNeeded;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;

@Mod.EventBusSubscriber
public final class DanmakuRegistry implements IInitNeeded {

	public static FMLControlledNamespacedRegistry<Form> FORM;
	public static FMLControlledNamespacedRegistry<SubEntityType> SUB_ENTITY;
	public static FMLControlledNamespacedRegistry<DanmakuVariant> DANMAKU_VARIANT;
	public static FMLControlledNamespacedRegistry<Spellcard> SPELLCARD;
	public static FMLControlledNamespacedRegistry<PhaseType> PHASE;

	@SubscribeEvent
	public static void createRegistries(RegistryEvent.NewRegistry event) {
		FORM = createRegistry(LibRegistryName.FORMS, Form.class, resource(LibFormName.DEFAULT));
		SUB_ENTITY = createRegistry(LibRegistryName.SUB_ENTITIES, SubEntityType.class, resource(LibSubEntityName.DEFAULT));
		DANMAKU_VARIANT = createRegistry(LibRegistryName.VARIANTS, DanmakuVariant.class, resource(LibDanmakuVariantName.DEFAULT));
		SPELLCARD = createRegistry(LibRegistryName.SPELLCARDS, Spellcard.class, null);
		PHASE = createRegistry(LibRegistryName.PHASES, PhaseType.class, resource(LibPhaseName.FALLBACK));
	}

	private static <I extends IForgeRegistryEntry<I>> FMLControlledNamespacedRegistry<I> createRegistry(ResourceLocation name, Class<I> clazz,
			@Nullable ResourceLocation defaultValue) {
		return PersistentRegistryManager.createRegistry(name, clazz, defaultValue, 0, Short.MAX_VALUE, false, null, null, null);
	}

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(LibMod.MODID, path);
	}
}
