/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.handler;

import net.katsstuff.danmakucore.EnumDanmakuLevel;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraftforge.common.config.Config;

@Config(modid = LibMod.MODID)
@SuppressWarnings({"WeakerAccess", "unused"})
public class ConfigHandler {

	public static Danmaku danmaku = new Danmaku();

	public static class Danmaku {

		public int danmakuMaxNumber = 32;
		public boolean oneHitKill = false;
		public EnumDanmakuLevel danmakuLevel = EnumDanmakuLevel.NORMAL;
	}
}
