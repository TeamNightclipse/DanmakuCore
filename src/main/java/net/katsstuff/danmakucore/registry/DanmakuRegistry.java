/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry;

import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class DanmakuRegistry {

	public static final FMLControlledNamespacedRegistry<Form> FORM =
			(FMLControlledNamespacedRegistry<Form>)GameRegistry.findRegistry(Form.class);
	public static final FMLControlledNamespacedRegistry<SubEntityType> SUB_ENTITY =
			(FMLControlledNamespacedRegistry<SubEntityType>)GameRegistry.findRegistry(SubEntityType.class);
	public static final FMLControlledNamespacedRegistry<DanmakuVariant> DANMAKU_VARIANT =
			(FMLControlledNamespacedRegistry<DanmakuVariant>)GameRegistry.findRegistry(DanmakuVariant.class);
	public static final FMLControlledNamespacedRegistry<Spellcard> SPELLCARD =
			(FMLControlledNamespacedRegistry<Spellcard>)GameRegistry.findRegistry(Spellcard.class);
	public static final FMLControlledNamespacedRegistry<PhaseType> PHASE =
			(FMLControlledNamespacedRegistry<PhaseType>)GameRegistry.findRegistry(PhaseType.class);
}
