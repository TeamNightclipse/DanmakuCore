/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.handler;

import net.katsstuff.teamnightclipse.danmakucore.EnumDanmakuLevel;
import net.katsstuff.teamnightclipse.danmakucore.lib.LibModJ;
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

			@Comment("Shows debug information for pathfinding")
			public boolean debugPathfinding = false;
		}

		@Comment("Should DanmakuCore use shaders for some danmaku? Note that not all Danmaku will work as well without shaders.")
		public boolean useShaders = true;
	}
}
