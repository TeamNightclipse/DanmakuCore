/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.EnumDyeColor;

@SuppressWarnings("unused")
public class LibColor {

	public static final int COLOR_VANILLA_WHITE = EnumDyeColor.WHITE.getMapColor().colorValue;
	public static final int COLOR_VANILLA_ORANGE = EnumDyeColor.ORANGE.getMapColor().colorValue;
	public static final int COLOR_VANILLA_MAGENTA = EnumDyeColor.MAGENTA.getMapColor().colorValue;
	public static final int COLOR_VANILLA_LIGHT_BLUE = EnumDyeColor.LIGHT_BLUE.getMapColor().colorValue;
	public static final int COLOR_VANILLA_YELLOW = EnumDyeColor.YELLOW.getMapColor().colorValue;
	public static final int COLOR_VANILLA_LIME = EnumDyeColor.LIME.getMapColor().colorValue;
	public static final int COLOR_VANILLA_PINK = EnumDyeColor.PINK.getMapColor().colorValue;
	public static final int COLOR_VANILLA_GRAY = EnumDyeColor.GRAY.getMapColor().colorValue;
	public static final int COLOR_VANILLA_SILVER = EnumDyeColor.SILVER.getMapColor().colorValue;
	public static final int COLOR_VANILLA_CYAN = EnumDyeColor.CYAN.getMapColor().colorValue;
	public static final int COLOR_VANILLA_PURPLE = EnumDyeColor.PURPLE.getMapColor().colorValue;
	public static final int COLOR_VANILLA_BLUE = EnumDyeColor.BLUE.getMapColor().colorValue;
	public static final int COLOR_VANILLA_BROWN = EnumDyeColor.BROWN.getMapColor().colorValue;
	public static final int COLOR_VANILLA_GREEN = EnumDyeColor.GREEN.getMapColor().colorValue;
	public static final int COLOR_VANILLA_RED = EnumDyeColor.RED.getMapColor().colorValue;
	public static final int COLOR_VANILLA_BLACK = EnumDyeColor.BLACK.getMapColor().colorValue;

	public static final int COLOR_SATURATED_RED     = 0xFF0000;
	public static final int COLOR_SATURATED_BLUE    = 0x0000FF;
	public static final int COLOR_SATURATED_GREEN   = 0x00FF00;
	public static final int COLOR_SATURATED_YELLOW  = 0xFFFF00;
	public static final int COLOR_SATURATED_MAGENTA = 0xFF00FF;
	public static final int COLOR_SATURATED_CYAN    = 0x00FFFF;
	public static final int COLOR_SATURATED_ORANGE  = 0xFF8C00;
	public static final int COLOR_WHITE             = COLOR_VANILLA_WHITE;

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
