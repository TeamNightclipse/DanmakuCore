/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibSpellcardName;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("WeakerAccess")
public final class LibSpellcards {

	public static final Spellcard DELUSION_OF_ENLIGHTENMENT;

	static {
		if(!DanmakuCore.registriesInitialized || !DanmakuCore.stuffRegistered) {
			throw new IllegalStateException("Forms were queried were they had been registered");
		}

		DELUSION_OF_ENLIGHTENMENT = getSpellcard(LibSpellcardName.DELUSION_OF_ENLIGHTENMENT);
	}

	@SuppressWarnings("ConstantConditions")
	private static Spellcard getSpellcard(String name) {
		Spellcard spellcard = DanmakuRegistry.INSTANCE.spellcard.getRegistry().getObject(new ResourceLocation(LibMod.MODID, name));

		if (spellcard == null) {
			throw new IllegalStateException("Invalid Spellcard requested: " + name);
		}
		else {
			return spellcard;
		}
	}
}
