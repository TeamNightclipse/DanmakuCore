/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import static net.katsstuff.danmakucore.lib.LibColor.COLOR_VANILLA_RED;

import net.katsstuff.danmakucore.data.ShotData;

public class LibShotData {

	public static final ShotData SHOT_TINY = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 0.5F, 0.1F);
	public static final ShotData SHOT_SMALL = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_MEDIUM = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 1.5F, 0.3F);
	public static final ShotData SHOT_SPHERE_DARK = new ShotData(LibForms.SPHERE_DARK, COLOR_VANILLA_RED, 1.8F, 0.3F);
	public static final ShotData SHOT_PELLET = new ShotData(LibForms.PELLET, COLOR_VANILLA_RED, 0.5F, 0.05F);
	public static final ShotData SHOT_CIRCLE = new ShotData(LibForms.SPHERE_CIRCLE, COLOR_VANILLA_RED, 1.5F, 0.3F);
	public static final ShotData SHOT_SCALE = new ShotData(LibForms.SCALE, COLOR_VANILLA_RED, 1.7F, 0.2F);
	public static final ShotData SHOT_SMALLSTAR = new ShotData(LibForms.STAR, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_STAR = new ShotData(LibForms.STAR, COLOR_VANILLA_RED, 2F, 0.4F);
	public static final ShotData SHOT_RICE = new ShotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 1F, 0.05F, 0.05F, 0.45F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_CRYSTAL1 = new ShotData(LibForms.CRYSTAL_1, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_CRYSTAL2 = new ShotData(LibForms.CRYSTAL_2, COLOR_VANILLA_RED, 1F, 0.2F);
	public static final ShotData SHOT_KUNAI = new ShotData(LibForms.KUNAI, COLOR_VANILLA_RED, 2F, 0.2F);
	public static final ShotData SHOT_OVAL = new ShotData(LibForms.SPHERE, COLOR_VANILLA_RED, 3F, 0.5F, 0.5F, 1F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_POINTED_LASER = new ShotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 3F, 0.2F, 0.2F, 1.25F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_POINTED_LASER_SHORT = new ShotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 1F, 0.2F, 0.2F, 1F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_POINTED_LASER_LONG = new ShotData(LibForms.SPHERE_POINTED, COLOR_VANILLA_RED, 2F, 0.2F, 0.2F, 1.5F, 0, 80, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_FIRE = new ShotData(LibForms.FIRE, COLOR_VANILLA_RED, 1.7F, 0.3F);
	public static final ShotData SHOT_LASER = new ShotData(LibForms.LASER, COLOR_VANILLA_RED, 1.5F, 0.5F, 0.5F, 15F, 30, 60, LibSubEntities.DEFAULT_TYPE);
	public static final ShotData SHOT_HEART = new ShotData(LibForms.HEART, COLOR_VANILLA_RED, 1.5F, 0.4F);
}
