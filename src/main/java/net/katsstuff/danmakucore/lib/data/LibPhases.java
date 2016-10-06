/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import java.util.HashSet;
import java.util.Set;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeSpellcard;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("WeakerAccess")
public class LibPhases {

	private static final Set<PhaseType> CACHE;
	public static final PhaseType FALLBACK; //Default
	public static final PhaseTypeSpellcard SPELLCARD;

	static {
		if(!DanmakuCore.registriesInitialized || !DanmakuCore.stuffRegistered) {
			throw new IllegalStateException("Forms were queried before they had been registered");
		}

		CACHE = new HashSet<>();

		FALLBACK = getPhase(LibPhaseName.FALLBACK); //Default
		SPELLCARD = (PhaseTypeSpellcard)getPhase(LibPhaseName.SPELLCARD);

		CACHE.clear();
	}

	private static PhaseType getPhase(String name) {
		PhaseType form = DanmakuRegistry.INSTANCE.phase.getRegistry().getObject(new ResourceLocation(LibMod.MODID, name));

		if (!CACHE.add(form)) {
			throw new IllegalStateException("Invalid Form requested: " + name);
		}
		else {
			return form;
		}
	}
}
