/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability;

public abstract class AbstractDanmakuCoreData implements IDanmakuCoreData {

	protected float power;
	protected int score;
	protected int lives;
	protected int bombs;

	@SuppressWarnings("WeakerAccess")
	public AbstractDanmakuCoreData(float power, int score, int lives, int bombs) {
		this.power = power;
		this.score = score;
		this.lives = lives;
		this.bombs = bombs;
	}

	@Override
	public float getPower() {
		return power;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public int getLives() {
		return lives;
	}

	@Override
	public int getBombs() {
		return bombs;
	}
}
