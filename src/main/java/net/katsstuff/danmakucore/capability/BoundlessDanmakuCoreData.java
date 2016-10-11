/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability;

public class BoundlessDanmakuCoreData implements IDanmakuCoreData {

	private float power;
	private int score;

	public BoundlessDanmakuCoreData(float power, int score) {
		this.power = power;
		this.score = score;
	}

	public BoundlessDanmakuCoreData() {
		this(0F, 0);
	}

	@Override
	public float getPower() {
		return power;
	}

	@Override
	public void setPower(float power) {
		this.power = power;
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
