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
import net.katsstuff.danmakucore.lib.LibModJ;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

@Config(modid = LibModJ.ID)
@SuppressWarnings({"WeakerAccess", "unused"})
public class ConfigHandler {

	public static Danmaku danmaku = new Danmaku();
	public static Gameplay gameplay = new Gameplay();
	public static Client client = new Client();

	public static class Danmaku {

		public int danmakuMaxNumber = 32;
		public boolean oneHitKill = false;
		@Comment({"Allowed values:", "PEACEFUL, EASY, NORMAL, HARD, LUNATIC, EXTRA, LAST_SPELL, LAST_WORD",
						 "Anything above EXTRA is not guaranteed to work as well"})
		public EnumDanmakuLevel danmakuLevel = EnumDanmakuLevel.NORMAL;
		public boolean useComplexHitbox = true;
	}

	public static class Gameplay {

		@Comment("If the bombs should be reset to the default amount each time the player looses a life")
		public boolean resetBombsOnDeath = true;
		public int defaultLivesAmount = 2;
		public int defaultBombsAmount = 2;
	}

	public static class Client {

		public HUD hud = new HUD();
		public Entities entities = new Entities();

		public static class HUD {

			public Power power = new Power();
			public Stars stars = new Stars();

			public static class Power {

				@Comment({"How many pixels before the power bar starts", "The size of the power bar is widthEnd - widthStart"})
				public int widthStart = 32;
				@Comment({"How many pixels before the power bar ends", "The size of the power bar is widthEnd - widthStart"})
				public int widthEnd = 128;

				public int posX = 27;
				public int posY = 29;

				@Comment("If the power (and score) bar should be hidden if it's full")
				public boolean hideIfFull = false;
			}

			public static class Stars {

				public int posX = 8;
				public int posY = 24;

				@Comment({"If the bomb/star bar should be hidden if", "both bombs and lives are above their high amounts (specified bellow)"})
				public boolean hideIfAboveHigh = false;
				public int livesHigh = 5;
				public int bombsHigh = 5;
			}
		}

		public static class Entities {

			@Comment("Currently not implemented")
			public boolean circleBossBar = false;
		}

		@Comment("Should DanmakuCore use shaders for some danmaku? Note that not all Danmaku will work as well without shaders.")
		public boolean useShaders = true;
	}
}
