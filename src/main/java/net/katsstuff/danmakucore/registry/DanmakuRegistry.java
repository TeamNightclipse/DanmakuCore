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
import java.util.Random;

import jline.internal.Nullable;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public final class DanmakuRegistry {

	public static final IForgeRegistry<Form> FORM = GameRegistry.findRegistry(Form.class);
	public static final IForgeRegistry<SubEntityType> SUB_ENTITY = GameRegistry.findRegistry(SubEntityType.class);
	public static final IForgeRegistry<DanmakuVariant> DANMAKU_VARIANT = GameRegistry.findRegistry(DanmakuVariant.class);
	public static final IForgeRegistry<Spellcard> SPELLCARD = GameRegistry.findRegistry(Spellcard.class);
	public static final IForgeRegistry<PhaseType> PHASE = GameRegistry.findRegistry(PhaseType.class);

	public static <T extends RegistryValue<T>> int getId(Class<T> clazz, T value) {
		return ((ForgeRegistry<T>)GameRegistry.findRegistry(clazz)).getID(value);
	}

	@Nullable
	public static <T extends RegistryValue<T>> T getObjById(Class<T> clazz, int id) {
		return ((ForgeRegistry<T>)GameRegistry.findRegistry(clazz)).getValue(id);
	}

	public static <T extends RegistryValue<T>> T getRandomObject(Class<T> clazz, Random rng) {
		List<T> values = GameRegistry.findRegistry(clazz).getValues();
		int idx = rng.nextInt(values.size());
		return values.get(idx);
	}
}
