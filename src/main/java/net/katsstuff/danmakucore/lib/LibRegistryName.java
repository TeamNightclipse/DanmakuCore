/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.lib;

import net.minecraft.util.ResourceLocation;

public class LibRegistryName {

	public static final ResourceLocation FORMS = resource("form");
	public static final ResourceLocation SUB_ENTITIES = resource("sub_entity");
	public static final ResourceLocation VARIANTS = resource("danmaku_variant");
	public static final ResourceLocation SPELLCARDS = resource("spellcard");
	public static final ResourceLocation PHASES = resource("phase");

	private static ResourceLocation resource(String path) {
		return new ResourceLocation(LibMod.MODID, path);
	}
}
