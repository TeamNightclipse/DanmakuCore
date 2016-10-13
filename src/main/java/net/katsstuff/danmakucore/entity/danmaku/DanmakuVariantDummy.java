/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;

public class DanmakuVariantDummy extends DanmakuVariant {

	@Override
	public ShotData getShotData() {
		return ShotData.DefaultShotData();
	}

	@Override
	public MovementData getMovementData() {
		return MovementData.constant(0D);
	}

	@Override
	public RotationData getRotationData() {
		return RotationData.none();
	}
}
