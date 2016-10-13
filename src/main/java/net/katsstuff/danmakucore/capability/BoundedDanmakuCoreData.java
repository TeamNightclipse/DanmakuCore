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

import net.minecraft.util.math.MathHelper;

@SuppressWarnings("WeakerAccess")
public class BoundedDanmakuCoreData implements IDanmakuCoreData {

	private float power;
	private int score;
	private int lives;
	private int bombs;
	private final float powerBound;

	public BoundedDanmakuCoreData(float power, int score, int lives, int bombs, float powerBound) {
		this.power = power;
		this.score = score;
		this.lives = lives;
		this.bombs = bombs;

		this.powerBound = powerBound;
	}

	public BoundedDanmakuCoreData(float powerBound) {
		this(0F, 0, 0, 0, powerBound);
	}

	public BoundedDanmakuCoreData() {
		this(4F);
	}

	@Override
	public float getPower() {
		return power;
	}

	@Override
	public void setPower(float power) {
		this.power = new BigDecimal(MathHelper.clamp_float(power, 0F, powerBound)).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
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
		this.lives = MathHelper.clamp_int(lives, 0, 9);
	}

	@Override
	public int getBombs() {
		return bombs;
	}

	@Override
	public void setBombs(int bombs) {
		this.bombs = MathHelper.clamp_int(bombs, 0, 9);
	}
}
