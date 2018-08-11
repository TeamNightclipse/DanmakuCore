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
package net.katsstuff.teamnightclipse.danmakucore;

/**
 * The different difficulty levels for danmaku
 */
@SuppressWarnings("unused")
public enum EnumDanmakuLevel {
	PEACEFUL(0),
	EASY(1),
	NORMAL(2),
	HARD(3),
	LUNATIC(5),
	EXTRA(7),
	LAST_SPELL(10),
	LAST_WORD(12);

	private final int multiplier;

	EnumDanmakuLevel(int multiplier) {
		this.multiplier = multiplier;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public static EnumDanmakuLevel cycle(EnumDanmakuLevel level) {
		switch(level) {
			case PEACEFUL:
				return EnumDanmakuLevel.EASY;
			case EASY:
				return EnumDanmakuLevel.NORMAL;
			case NORMAL:
				return EnumDanmakuLevel.HARD;
			case HARD:
				return EnumDanmakuLevel.LUNATIC;
			case LUNATIC:
				return EnumDanmakuLevel.EXTRA;
			default:
				return EnumDanmakuLevel.EASY;
		}
	}
}
