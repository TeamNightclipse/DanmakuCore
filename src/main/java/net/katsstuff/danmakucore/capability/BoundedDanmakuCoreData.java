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

import com.google.common.base.MoreObjects;

import net.minecraft.util.math.MathHelper;

@SuppressWarnings("WeakerAccess")
public class BoundedDanmakuCoreData extends AbstractDanmakuCoreData {

	private final float powerBound;
	private final int lifeBombBound;

	public BoundedDanmakuCoreData(float power, int score, int lives, int bombs, float powerBound, int lifeBombBound) {
		super(power, score, lives, bombs);

		this.powerBound = powerBound;
		this.lifeBombBound = lifeBombBound;
	}

	public BoundedDanmakuCoreData(float powerBound, int lifeBombBound) {
		this(0F, 0, 0, 0, powerBound, lifeBombBound);
	}

	public BoundedDanmakuCoreData() {
		this(4F, 9);
	}

	@Override
	public void setPower(float power) {
		this.power = BigDecimal.valueOf(MathHelper.clamp(power, 0F, powerBound)).setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	@Override
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void setLives(int lives) {
		this.lives = MathHelper.clamp(lives, 0, lifeBombBound);
	}

	@Override
	public void setBombs(int bombs) {
		this.bombs = MathHelper.clamp(bombs, 0, lifeBombBound);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("powerBound", powerBound).add("lifeBombBound", lifeBombBound).add("power", power).add("score",
				score).add("lives", lives).add("bombs", bombs).toString();
	}
}
