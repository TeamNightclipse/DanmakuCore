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

import static net.katsstuff.teamnightclipse.danmakucore.lib.LibColor.COLOR_VANILLA_RED;

import java.awt.Color;

import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.Form;
import net.katsstuff.teamnightclipse.danmakucore.data.ShotData;

public class LibShotData {

	public static final ShotData SHOT_TINY = shotData(LibForms.SPHERE, COLOR_VANILLA_RED, 0.5F, 0.1F);
	public static final ShotData SHOT_SMALL = shotData(LibForms.SPHERE, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_MEDIUM = shotData(LibForms.SPHERE, COLOR_VANILLA_RED, 1.5F, 0.3F);
	public static final ShotData SHOT_SPHERE_DARK = shotData(LibForms.SPHERE, COLOR_VANILLA_RED, 1.8F, 0.3F).setCoreColor(0x000000);
	public static final ShotData SHOT_CIRCLE = shotData(LibForms.SPHERE_CIRCLE, COLOR_VANILLA_RED, 1.5F, 0.3F);
	public static final ShotData SHOT_SCALE = shotData(LibForms.SCALE, COLOR_VANILLA_RED, 1.7F, 0.2F);
	public static final ShotData SHOT_SMALLSTAR = shotData(LibForms.STAR, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_STAR = shotData(LibForms.STAR, COLOR_VANILLA_RED, 2F, 0.4F);
	public static final ShotData SHOT_RICE = shotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 1F, 0.05F, 0.05F, 0.45F);
	public static final ShotData SHOT_CRYSTAL1 = shotData(LibForms.CRYSTAL_1, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_CRYSTAL2 = shotData(LibForms.CRYSTAL_2, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_KUNAI = shotData(LibForms.KUNAI, COLOR_VANILLA_RED, 2F, 0.2F);
	public static final ShotData SHOT_OVAL = shotData(LibForms.SPHERE, COLOR_VANILLA_RED, 3F, 0.5F, 0.5F, 1F);
	public static final ShotData SHOT_POINTED_LASER = shotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 3F, 0.2F, 0.2F, 1.25F);
	public static final ShotData SHOT_POINTED_LASER_SHORT = shotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 1F, 0.2F, 0.2F, 1F);
	public static final ShotData SHOT_POINTED_LASER_LONG = shotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 2F, 0.2F, 0.2F, 1.5F);
	public static final ShotData SHOT_FIRE = shotData(LibForms.FIRE, COLOR_VANILLA_RED, 1.7F, 0.3F);
	public static final ShotData SHOT_LASER = shotData(LibForms.LASER, COLOR_VANILLA_RED, 1.5F, 0.5F, 0.5F, 15F).setDelay(30).setEnd(60);
	public static final ShotData SHOT_HEART = shotData(LibForms.HEART, COLOR_VANILLA_RED, 1.5F, 0.4F);
	public static final ShotData SHOT_NOTE1 = shotData(LibForms.NOTE1, COLOR_VANILLA_RED, 1.3F, 0.4F);
	public static final ShotData SHOT_BUBBLE = shotData(LibForms.BUBBLE, 0xFFFFFF, 1.3F, 1F).setCoreColor(COLOR_VANILLA_RED);
	
	private static ShotData shotData(Form form, int color, float damage, float size) {
		return ShotData.DefaultShotData().setForm(form).setEdgeColor(color).setDamage(damage).setSize(size);
	}

	private static ShotData shotData(Form form, int color, float damage, float sizeX, float sizeY, float sizeZ) {
		return ShotData.DefaultShotData().setForm(form).setEdgeColor(color).setDamage(damage).setSize(sizeX, sizeY, sizeZ);
	}

	public static ShotData pellet(int color) {
		ShotData base = ShotData.DefaultShotData().setForm(LibForms.SPHERE).setEdgeColor(color).setDamage(0.5F).setSize(0.05F);

		Color colorObj = new Color(color);
		float[] hsb      = new float[3];
		Color.RGBtoHSB(colorObj.getRed(), colorObj.getGreen(), colorObj.getBlue(), hsb);
		hsb[1] = 0.15F;
		hsb[2] = 1.0F;
		int coreColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2]).getRGB();

		return base.setCoreColor(coreColor);
	}
}
