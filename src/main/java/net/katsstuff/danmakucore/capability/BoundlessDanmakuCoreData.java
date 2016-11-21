/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability;

import java.math.BigDecimal;

public class BoundlessDanmakuCoreData implements IDanmakuCoreData {

	private float power;
	private int score;
	private int lives;
	private int bombs;

	@SuppressWarnings("WeakerAccess")
	public BoundlessDanmakuCoreData(float power, int score, int lives, int bombs) {
		this.power = power;
		this.score = score;
		this.lives = lives;
		this.bombs = bombs;
	}

	public BoundlessDanmakuCoreData() {
		this(0F, 0, 0, 0);
	}

	@Override
	public float getPower() {
		return power;
	}

	@Override
	public void setPower(float power) {
		this.power = new BigDecimal(power).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public int getLives() {
		return lives;
	}

	@Override
	public void setLives(int lives) {
		this.lives = lives;
	}

	@Override
	public int getBombs() {
		return bombs;
	}

	@Override
	public void setBombs(int bombs) {
		this.bombs = bombs;
	}

	@Override
	public String toString() {
		return "BoundlessDanmakuCoreData{" + "power=" + power + ", score=" + score + ", lives=" + lives + ", bombs=" + bombs + '}';
	}
}
