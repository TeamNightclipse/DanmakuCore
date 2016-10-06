/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.handler;

import java.io.File;

import net.katsstuff.danmakucore.EnumDanmakuLevel;
import net.katsstuff.danmakucore.lib.LibMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {

	private static final String CATEGORY_DANMAKU = "Danmaku";
	private static Configuration cfg;

	private static int danmakuMaxNumber;
	private static boolean oneHitKill;
	private static EnumDanmakuLevel danmakuLevel;

	public static void setConfig(File configFile) {
		cfg = new Configuration(configFile);
		cfg.load();
		loadConfig();

		MinecraftForge.EVENT_BUS.register(new ChangeListener());
	}

	private static void loadConfig() {
		danmakuMaxNumber = cfg.get(CATEGORY_DANMAKU, "Max Danmaku number", 32).getInt();
		oneHitKill = cfg.get(CATEGORY_DANMAKU, "One Hit Kill", false).getBoolean();

		int danmakuLevelInt = cfg.get(CATEGORY_DANMAKU, "Danmaku Difficulty", 2).getInt();
		switch(danmakuLevelInt) {
			case 0:
				danmakuLevel = EnumDanmakuLevel.PEACEFUL;
				break;
			case 1:
				danmakuLevel = EnumDanmakuLevel.EASY;
				break;
			case 2:
				danmakuLevel = EnumDanmakuLevel.NORMAL;
				break;
			case 3:
				danmakuLevel = EnumDanmakuLevel.HARD;
				break;
			case 4:
				danmakuLevel = EnumDanmakuLevel.LUNATIC;
				break;
			default:
				break;
		}

		if(cfg.hasChanged()) {
			cfg.save();
		}
	}

	public static int getDanmakuMaxNumber() {
		return danmakuMaxNumber;
	}

	public static boolean isOneHitKill() {
		return oneHitKill;
	}

	public static EnumDanmakuLevel getDanmakuLevel() {
		return danmakuLevel;
	}

	public static class ChangeListener {

		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if(eventArgs.getModID().equals(LibMod.MODID)) {
				loadConfig();
			}
		}
	}
}
