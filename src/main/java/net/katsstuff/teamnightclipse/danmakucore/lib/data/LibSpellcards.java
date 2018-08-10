/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.lib.data;

import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.SpellcardDummy;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibModJ;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibSpellcardName;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public final class LibSpellcards {

	@ObjectHolder(LibSpellcardName.DELUSION_OF_ENLIGHTENMENT)
	public static final Spellcard DELUSION_OF_ENLIGHTENMENT = SpellcardDummy.instance();
}
