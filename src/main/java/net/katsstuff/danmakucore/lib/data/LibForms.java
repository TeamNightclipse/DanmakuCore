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
import net.katsstuff.danmakucore.entity.danmaku.form.FormDummy;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.lib.LibModJ;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public final class LibForms {

	@ObjectHolder(LibFormName.DEFAULT)
	public static final Form SPHERE = new FormDummy(); //Default
	@ObjectHolder(LibFormName.SPHERE_DARK)
	public static final Form SPHERE_DARK = new FormDummy();
	@ObjectHolder(LibFormName.CRYSTAL1)
	public static final Form CRYSTAL_1 = new FormDummy();
	@ObjectHolder(LibFormName.CRYSTAL2)
	public static final Form CRYSTAL_2 = new FormDummy();
	@ObjectHolder(LibFormName.CIRCLE)
	public static final Form SPHERE_CIRCLE = new FormDummy();
	@ObjectHolder(LibFormName.PELLET)
	public static final Form PELLET = new FormDummy();
	@ObjectHolder(LibFormName.SCALE)
	public static final Form SCALE = new FormDummy();
	@ObjectHolder(LibFormName.STAR)
	public static final Form STAR = new FormDummy();
	@ObjectHolder(LibFormName.KUNAI)
	public static final Form KUNAI = new FormDummy();
	@ObjectHolder(LibFormName.SPHERE_POINTED)
	public static final Form SPHERE_POINTED = new FormDummy();
	@ObjectHolder(LibFormName.CONTROL)
	public static final Form CONTROL = new FormDummy();
	@ObjectHolder(LibFormName.FIRE)
	public static final Form FIRE = new FormDummy();
	@ObjectHolder(LibFormName.LASER)
	public static final Form LASER = new FormDummy();
	@ObjectHolder(LibFormName.HEART)
	public static final Form HEART = new FormDummy();
	@ObjectHolder(LibFormName.NOTE1)
	public static final Form NOTE1 = new FormDummy();
}
