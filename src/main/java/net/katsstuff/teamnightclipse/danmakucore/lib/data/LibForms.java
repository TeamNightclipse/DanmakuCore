/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.lib.data;

import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.Form;
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.FormDummy;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibFormName;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibModJ;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public final class LibForms {

	@ObjectHolder(LibFormName.DEFAULT)
	public static final Form SPHERE = FormDummy.instance(); //Default
	@ObjectHolder(LibFormName.CRYSTAL1)
	public static final Form CRYSTAL_1 = FormDummy.instance();
	@ObjectHolder(LibFormName.CRYSTAL2)
	public static final Form CRYSTAL_2 = FormDummy.instance();
	@ObjectHolder(LibFormName.CIRCLE)
	public static final Form SPHERE_CIRCLE = FormDummy.instance();
	@ObjectHolder(LibFormName.SCALE)
	public static final Form SCALE = FormDummy.instance();
	@ObjectHolder(LibFormName.STAR)
	public static final Form STAR = FormDummy.instance();
	@ObjectHolder(LibFormName.KUNAI)
	public static final Form KUNAI = FormDummy.instance();
	@ObjectHolder(LibFormName.SPHERE_POINTED)
	public static final Form SPHERE_POINTED = FormDummy.instance();
	@ObjectHolder(LibFormName.CONTROL)
	public static final Form CONTROL = FormDummy.instance();
	@ObjectHolder(LibFormName.FIRE)
	public static final Form FIRE = FormDummy.instance();
	@ObjectHolder(LibFormName.LASER)
	public static final Form LASER = FormDummy.instance();
	@ObjectHolder(LibFormName.HEART)
	public static final Form HEART = FormDummy.instance();
	@ObjectHolder(LibFormName.NOTE1)
	public static final Form NOTE1 = FormDummy.instance();
	@ObjectHolder(LibFormName.BUBBLE)
	public static final Form BUBBLE = FormDummy.instance();
}
