/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.spellcard;

import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.lib.LibMod;

@SuppressWarnings("WeakerAccess")
public abstract class SpellcardCore extends Spellcard {

	private final String name;

	public SpellcardCore(String name) {
		super(name);
		this.name = name;
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName() + "." + LibMod.MODID +  "." + name;
	}
}
