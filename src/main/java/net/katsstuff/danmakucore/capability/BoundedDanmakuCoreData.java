/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability;

import net.minecraft.util.math.MathHelper;

public class BoundedDanmakuCoreData implements IDanmakuCoreData {

	private float power;
	private int score;
	private final float powerBound;

	public BoundedDanmakuCoreData(float power, int score, float powerBound) {
		this.power = power;
		this.score = score;
		this.powerBound = powerBound;
	}

	public BoundedDanmakuCoreData(float powerBound) {
		this(0F, 0, powerBound);
	}

	public BoundedDanmakuCoreData() {
		this(0F, 0, 4F);
	}

	@Override
	public float getPower() {
		return power;
	}

	@Override
	public void setPower(float power) {
		this.power = MathHelper.clamp_float(power, 0F, powerBound);
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public void setScore(int score) {
		this.score = score;
	}
}
