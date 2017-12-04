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
import net.katsstuff.danmakucore.lib.LibModJ;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibModJ.ID)
public final class LibDanmakuVariants {

	@ObjectHolder(LibDanmakuVariantName.DEFAULT)
	public static final DanmakuVariant DEFAULT_TYPE = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.CIRCLE)
	public static final DanmakuVariant CIRCLE = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.CRYSTAL1)
	public static final DanmakuVariant CRYSTAL1 = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.CRYSTAL2)
	public static final DanmakuVariant CRYSTAL2 = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.OVAL)
	public static final DanmakuVariant OVAL = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.SPHERE_DARK)
	public static final DanmakuVariant SPHERE_DARK = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.PELLET)
	public static final DanmakuVariant PELLET = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.TINY)
	public static final DanmakuVariant TINY = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.SMALL)
	public static final DanmakuVariant SMALL = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.STAR)
	public static final DanmakuVariant STAR = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.STAR_SMALL)
	public static final DanmakuVariant STAR_SMALL = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.KUNAI)
	public static final DanmakuVariant KUNAI = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.SCALE)
	public static final DanmakuVariant SCALE = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.RICE)
	public static final DanmakuVariant RICE = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.POINTED_LASER)
	public static final DanmakuVariant POINTED_LASER = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.POINTED_LASER_SHORT)
	public static final DanmakuVariant POINTED_LASER_SHORT = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.POINTED_LASER_LONG)
	public static final DanmakuVariant POINTED_LASER_LONG = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.LASER)
	public static final DanmakuVariant LASER = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.HEART)
	public static final DanmakuVariant HEART = DanmakuVariantDummy.instance();
	@ObjectHolder(LibDanmakuVariantName.NOTE1)
	public static final DanmakuVariant NOTE1 = DanmakuVariantDummy.instance();
}
