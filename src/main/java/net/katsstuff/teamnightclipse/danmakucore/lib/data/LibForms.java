/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
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
	@ObjectHolder(LibFormName.TALISMAN)
	public static final Form TALISMAN = FormDummy.instance();
}
