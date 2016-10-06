/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.katsstuff.danmakucore.data.ShotData;
import net.minecraft.item.EnumDyeColor;

public class LibShotData {

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

	//@formatter:off
	public static final int COLOR_SATURATED_RED     = 0xFF0000;
	public static final int COLOR_SATURATED_BLUE    = 0x0000FF;
	public static final int COLOR_SATURATED_GREEN   = 0x00FF00;
	public static final int COLOR_SATURATED_YELLOW  = 0xFFFF00;
	public static final int COLOR_SATURATED_MAGENTA = 0xFF00FF;
	public static final int COLOR_SATURATED_CYAN    = 0x00FFFF;
	public static final int COLOR_SATURATED_ORANGE  = 0xFF8C00;
	public static final int COLOR_WHITE             = 0xFFFFFF; //TODO: Needed?
	//@formatter:on

	//@formatter:off
	public static final ShotData SHOT_TINY = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 0.5F, 0.1F);
	public static final ShotData SHOT_SMALL = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 0.8F, 0.2F);
	public static final ShotData SHOT_MEDIUM = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 1F, 0.3F);
	public static final ShotData SHOT_SPHERE_DARK = new ShotData(LibForms.SPHERE_DARK, COLOR_VANILLA_RED, 1F, 0.3F);
	public static final ShotData SHOT_PELLET = new ShotData(LibForms.PELLET, COLOR_VANILLA_RED, 0.5F, 0.05F);
	public static final ShotData SHOT_CIRCLE = new ShotData(LibForms.SPHERE_CIRCLE, COLOR_VANILLA_RED, 1.5F, 0.3F);
	public static final ShotData SHOT_SCALE = new ShotData(LibForms.SCALE, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_SMALLSTAR = new ShotData(LibForms.STAR, COLOR_VANILLA_RED, 0.5F, 0.2F);
	public static final ShotData SHOT_STAR = new ShotData(LibForms.STAR, COLOR_VANILLA_RED, 1F, 0.4F);
	public static final ShotData SHOT_RICE = new ShotData(LibForms.RICE, COLOR_VANILLA_RED, 0.5F, 0.05F, 0.05F, 0.45F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_CRYSTAL1 = new ShotData(LibForms.CRYSTAL_1, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_CRYSTAL2 = new ShotData(LibForms.CRYSTAL_2, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_KUNAI = new ShotData(LibForms.KUNAI, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_OVAL = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 2F, 0.5F, 0.5F, 1F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_LASER = new ShotData(LibForms.LASER, COLOR_VANILLA_RED, 1.5F, 0.2F, 0.2F, 1.25F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_LASER_SHORT = new ShotData(LibForms.LASER, COLOR_VANILLA_RED, 1F, 0.2F, 0.2F, 1F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_LASER_LONG = new ShotData(LibForms.LASER, COLOR_VANILLA_RED, 2F, 0.2F, 0.2F, 1.5F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	//@formatter:on

	//TODO: Better place for this
	public static final double GRAVITY_DEFAULT = -0.03D;

	private static final List<Integer> VALID_COLOR = new ArrayList<>();

	//TODO: Better place for this?
	/**
	 * Registers a color so that you can specify a custom localization for it.
	 *
	 * @param color The color you want to register.
	 */
	public static void registerColor(int color) {
		VALID_COLOR.add(color);
	}

	/**
	 * @return Gives you a list of all the registered colors.
	 */
	public static List<Integer> getRegisteredColors() {
		return ImmutableList.copyOf(VALID_COLOR);
	}
}
