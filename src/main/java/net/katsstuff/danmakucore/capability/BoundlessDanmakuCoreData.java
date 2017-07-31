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

public class BoundlessDanmakuCoreData extends AbstractDanmakuCoreData {

	@SuppressWarnings("WeakerAccess")
	public BoundlessDanmakuCoreData(float power, int score, int lives, int bombs) {
		super(power, score, lives, bombs);
	}

	public BoundlessDanmakuCoreData() {
		this(0F, 0, 0, 0);
	}

	@Override
	public void setPower(float power) {
		this.power = BigDecimal.valueOf(power).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	@Override
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void setLives(int lives) {
		this.lives = lives;
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
