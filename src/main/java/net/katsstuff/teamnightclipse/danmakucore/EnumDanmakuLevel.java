/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
