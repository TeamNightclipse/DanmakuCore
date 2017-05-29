/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariantDummy;
import net.katsstuff.danmakucore.lib.LibDanmakuVariantName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibMod.MODID)
public final class LibDanmakuVariants {

	@ObjectHolder(LibDanmakuVariantName.DEFAULT)
	public static final DanmakuVariant DEFAULT_TYPE = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.CIRCLE)
	public static final DanmakuVariant CIRCLE = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.CRYSTAL1)
	public static final DanmakuVariant CRYSTAL1 = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.CRYSTAL2)
	public static final DanmakuVariant CRYSTAL2 = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.OVAL)
	public static final DanmakuVariant OVAL = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.SPHERE_DARK)
	public static final DanmakuVariant SPHERE_DARK = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.PELLET)
	public static final DanmakuVariant PELLET = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.TINY)
	public static final DanmakuVariant TINY = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.SMALL)
	public static final DanmakuVariant SMALL = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.STAR)
	public static final DanmakuVariant STAR = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.STAR_SMALL)
	public static final DanmakuVariant STAR_SMALL = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.KUNAI)
	public static final DanmakuVariant KUNAI = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.SCALE)
	public static final DanmakuVariant SCALE = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.RICE)
	public static final DanmakuVariant RICE = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.POINTED_LASER)
	public static final DanmakuVariant POINTED_LASER = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.POINTED_LASER_SHORT)
	public static final DanmakuVariant POINTED_LASER_SHORT = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.POINTED_LASER_LONG)
	public static final DanmakuVariant POINTED_LASER_LONG = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.LASER)
	public static final DanmakuVariant LASER = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.HEART)
	public static final DanmakuVariant HEART = new DanmakuVariantDummy();
	@ObjectHolder(LibDanmakuVariantName.NOTE1)
	public static final DanmakuVariant NOTE1 = new DanmakuVariantDummy();
}
