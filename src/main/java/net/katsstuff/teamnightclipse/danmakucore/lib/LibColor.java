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
package net.katsstuff.teamnightclipse.danmakucore.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@SuppressWarnings("unused")
public class LibColor {

	public static final int COLOR_VANILLA_WHITE = getColor(EnumDyeColor.WHITE);
	public static final int COLOR_VANILLA_ORANGE = getColor(EnumDyeColor.ORANGE);
	public static final int COLOR_VANILLA_MAGENTA = getColor(EnumDyeColor.MAGENTA);
	public static final int COLOR_VANILLA_LIGHT_BLUE = getColor(EnumDyeColor.LIGHT_BLUE);
	public static final int COLOR_VANILLA_YELLOW = getColor(EnumDyeColor.YELLOW);
	public static final int COLOR_VANILLA_LIME = getColor(EnumDyeColor.LIME);
	public static final int COLOR_VANILLA_PINK = getColor(EnumDyeColor.PINK);
	public static final int COLOR_VANILLA_GRAY = getColor(EnumDyeColor.GRAY);
	public static final int COLOR_VANILLA_SILVER = getColor(EnumDyeColor.SILVER);
	public static final int COLOR_VANILLA_CYAN = getColor(EnumDyeColor.CYAN);
	public static final int COLOR_VANILLA_PURPLE = getColor(EnumDyeColor.PURPLE);
	public static final int COLOR_VANILLA_BLUE = getColor(EnumDyeColor.BLUE);
	public static final int COLOR_VANILLA_BROWN = getColor(EnumDyeColor.BROWN);
	public static final int COLOR_VANILLA_GREEN = getColor(EnumDyeColor.GREEN);
	public static final int COLOR_VANILLA_RED = getColor(EnumDyeColor.RED);
	public static final int COLOR_VANILLA_BLACK = getColor(EnumDyeColor.BLACK);

	public static final int COLOR_SATURATED_RED     = 0xFF0000;
	public static final int COLOR_SATURATED_BLUE    = 0x0000FF;
	public static final int COLOR_SATURATED_GREEN   = 0x00FF00;
	public static final int COLOR_SATURATED_YELLOW  = 0xFFFF00;
	public static final int COLOR_SATURATED_MAGENTA = 0xFF00FF;
	public static final int COLOR_SATURATED_CYAN    = 0x00FFFF;
	public static final int COLOR_SATURATED_ORANGE  = 0xFF8C00;
	public static final int COLOR_WHITE             = COLOR_VANILLA_WHITE;

	private static final int[] SATURATED_COLORS = new int[] {LibColor.COLOR_SATURATED_BLUE, LibColor.COLOR_SATURATED_CYAN,
			LibColor.COLOR_SATURATED_GREEN, LibColor.COLOR_SATURATED_MAGENTA, LibColor.COLOR_SATURATED_ORANGE, LibColor.COLOR_SATURATED_RED,
			LibColor.COLOR_WHITE, LibColor.COLOR_SATURATED_YELLOW};

	private static final int[] VANILLA_COLORS = new int[] {LibColor.COLOR_VANILLA_WHITE, LibColor.COLOR_VANILLA_ORANGE,
			LibColor.COLOR_VANILLA_MAGENTA, LibColor.COLOR_VANILLA_LIGHT_BLUE, LibColor.COLOR_VANILLA_YELLOW, LibColor.COLOR_VANILLA_LIME,
			LibColor.COLOR_VANILLA_PINK, LibColor.COLOR_VANILLA_GRAY, LibColor.COLOR_VANILLA_SILVER, LibColor.COLOR_VANILLA_CYAN,
			LibColor.COLOR_VANILLA_PURPLE, LibColor.COLOR_VANILLA_BLUE, LibColor.COLOR_VANILLA_BROWN, LibColor.COLOR_VANILLA_GREEN,
			LibColor.COLOR_VANILLA_RED, LibColor.COLOR_VANILLA_BLACK};

	private static int getColor(EnumDyeColor color) {
		return ReflectionHelper.getPrivateValue(EnumDyeColor.class, color, "colorValue", "field_176787_t");
	}

	public static int[] saturatedColors() {
		return Arrays.copyOf(SATURATED_COLORS, SATURATED_COLORS.length);
	}

	public static int[] vanillaColors() {
		return Arrays.copyOf(VANILLA_COLORS, VANILLA_COLORS.length);
	}

	/**
	 * Check if a color is registered
	 */
	public static boolean isNormalColor(int color) {
		return VALID_COLOR.contains(color);
	}

	public static int randomSaturatedColor() {
		return SATURATED_COLORS[ThreadLocalRandom.current().nextInt(SATURATED_COLORS.length)];
	}

	public static int randomVanillaColor() {
		return VANILLA_COLORS[ThreadLocalRandom.current().nextInt(VANILLA_COLORS.length)];
	}

	private static final List<Integer> VALID_COLOR = new ArrayList<>();

	/**
	 * Registers a color so that you can specify a custom localization for it.
	 *
	 * @param color The color you want to register.
	 */
	public static void registerColor(int color) {
		VALID_COLOR.add(color);
	}

	/**
	 * @return Gives you a list of all the registered SATURATED_COLORS.
	 */
	public static List<Integer> getRegisteredColors() {
		return ImmutableList.copyOf(VALID_COLOR);
	}
}
