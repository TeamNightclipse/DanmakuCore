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
import net.katsstuff.danmakucore.impl.phase.PhaseTypeShapeCircle;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeShapeRing;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeShapeWide;
import net.katsstuff.danmakucore.impl.phase.PhaseTypeSpellcard;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibModJ;
import net.katsstuff.danmakucore.lib.LibPhaseName;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public class LibPhases {

	@ObjectHolder(LibPhaseName.FALLBACK)
	public static final PhaseType FALLBACK = new PhaseTypeDummy(); //Default
	@ObjectHolder(LibPhaseName.SPELLCARD)
	public static final PhaseTypeSpellcard SPELLCARD = null;

	@ObjectHolder(LibPhaseName.SHAPE_CIRCLE)
	public static final PhaseTypeShapeCircle SHAPE_CIRCLE = null;
	@ObjectHolder(LibPhaseName.SHAPE_RING)
	public static final PhaseTypeShapeRing SHAPE_RING = null;
	@ObjectHolder(LibPhaseName.SHAPE_WIDE)
	public static final PhaseTypeShapeWide SHAPE_WIDE = null;
}
