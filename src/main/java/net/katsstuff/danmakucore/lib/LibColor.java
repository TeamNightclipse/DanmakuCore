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

	private static int getColor(EnumDyeColor color) {
		return ReflectionHelper.getPrivateValue(EnumDyeColor.class, color, "colorValue", "field_176787_t");
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
