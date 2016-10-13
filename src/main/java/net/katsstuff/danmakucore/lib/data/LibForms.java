/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import net.katsstuff.danmakucore.entity.danmaku.form.Form;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.misc.IInitNeeded;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibMod.MODID)
public final class LibForms implements IInitNeeded {

	@ObjectHolder(LibFormName.DEFAULT)
	public static final Form SPHERE = null; //Default
	@ObjectHolder(LibFormName.SPHERE_DARK)
	public static final Form SPHERE_DARK = null;
	@ObjectHolder(LibFormName.CRYSTAL1)
	public static final Form CRYSTAL_1 = null;
	@ObjectHolder(LibFormName.CRYSTAL2)
	public static final Form CRYSTAL_2 = null;
	@ObjectHolder(LibFormName.CIRCLE)
	public static final Form SPHERE_CIRCLE = null;
	@ObjectHolder(LibFormName.PELLET)
	public static final Form PELLET = null;
	@ObjectHolder(LibFormName.SCALE)
	public static final Form SCALE = null;
	@ObjectHolder(LibFormName.STAR)
	public static final Form STAR = null;
	@ObjectHolder(LibFormName.KUNAI)
	public static final Form KUNAI = null;
	@ObjectHolder(LibFormName.SPHERE_POINTED)
	public static final Form SPHERE_POINTED = null;
}
