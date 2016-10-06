/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib.data;

import java.util.HashSet;
import java.util.Set;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.lib.LibDanmakuVariantName;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("WeakerAccess")
public final class LibDanmakuVariants {

	private static final Set<DanmakuVariant> CACHE;
	public static final DanmakuVariant DEFAULT_TYPE;
	public static final DanmakuVariant CIRCLE;
	public static final DanmakuVariant CRYSTAL1;
	public static final DanmakuVariant CRYSTAL2;
	public static final DanmakuVariant OVAL;
	public static final DanmakuVariant SPHERE_DARK;
	public static final DanmakuVariant PELLET;
	public static final DanmakuVariant TINY;
	public static final DanmakuVariant SMALL;
	public static final DanmakuVariant STAR_SMALL;
	public static final DanmakuVariant STAR;
	public static final DanmakuVariant KUNAI;

	public static final DanmakuVariant SCALE;
	public static final DanmakuVariant RICE;
	public static final DanmakuVariant LASER;
	public static final DanmakuVariant LASER_SHORT;
	public static final DanmakuVariant LASER_LONG;

	static {
		if(!DanmakuCore.registriesInitialized || !DanmakuCore.variantsRegistered) {
			throw new IllegalStateException("Danmaku Variants were they had been registered");
		}

		CACHE = new HashSet<>();

		DEFAULT_TYPE = getVariant(LibDanmakuVariantName.DEFAULT);
		CIRCLE = getVariant(LibDanmakuVariantName.CIRCLE);
		CRYSTAL1 = getVariant(LibDanmakuVariantName.CRYSTAL1);
		CRYSTAL2 = getVariant(LibDanmakuVariantName.CRYSTAL2);
		OVAL = getVariant(LibDanmakuVariantName.OVAL);
		SPHERE_DARK = getVariant(LibDanmakuVariantName.SPHERE_DARK);
		PELLET = getVariant(LibDanmakuVariantName.PELLET);
		TINY = getVariant(LibDanmakuVariantName.TINY);
		SMALL = getVariant(LibDanmakuVariantName.SMALL);
		STAR_SMALL = getVariant(LibDanmakuVariantName.STAR_SMALL);
		STAR = getVariant(LibDanmakuVariantName.STAR);
		KUNAI = getVariant(LibDanmakuVariantName.KUNAI);

		SCALE = getVariant(LibDanmakuVariantName.SCALE);
		RICE = getVariant(LibDanmakuVariantName.RICE);
		LASER = getVariant(LibDanmakuVariantName.LASER);
		LASER_SHORT = getVariant(LibDanmakuVariantName.LASER_SHORT);
		LASER_LONG = getVariant(LibDanmakuVariantName.LASER_LONG);

		CACHE.clear();
	}

	private static DanmakuVariant getVariant(String name) {
		DanmakuVariant variant = DanmakuRegistry.INSTANCE.danmakuVariant.getRegistry().getObject(new ResourceLocation(LibMod.MODID, name));

		if (!CACHE.add(variant)) {
			throw new IllegalStateException("Invalid Danmaku Variant requested: " + name);
		}
		else {
			return variant;
		}
	}
}
