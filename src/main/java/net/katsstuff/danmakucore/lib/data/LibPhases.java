/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.entity.living.phase.PhaseTypeDummy;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeSpellcard;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibMod.MODID)
public class LibPhases {

	@ObjectHolder(LibPhaseName.FALLBACK)
	public static final PhaseType FALLBACK = new PhaseTypeDummy(); //Default
	@ObjectHolder(LibPhaseName.SPELLCARD)
	public static final PhaseTypeSpellcard SPELLCARD = null;
}
