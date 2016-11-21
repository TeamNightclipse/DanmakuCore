/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.DanmakuVariant;

import java.util.function.Supplier;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;

public class DanmakuVariantCoreGeneric extends DanmakuVariant {

	private ShotData shotData = null;
	private final MovementData movement;
	private final RotationData rotation = RotationData.none();

	private final Supplier<ShotData> shotDataSupplier;

	@SuppressWarnings("WeakerAccess")
	public DanmakuVariantCoreGeneric(String name, Supplier<ShotData> shotData, MovementData movement) {
		super(name);
		shotDataSupplier = shotData;
		this.movement = movement;
	}

	public DanmakuVariantCoreGeneric(String name, Supplier<ShotData> shotData, double speed) {
		this(name, shotData, new MovementData(speed, speed, 0.0D, Vector3.GravityZero()));
	}

	public DanmakuVariantCoreGeneric(MovementData movement, Supplier<ShotData> shotDataSupplier) {
		this.movement = movement;
		this.shotDataSupplier = shotDataSupplier;
	}

	@Override
	public ShotData getShotData() {
		if(shotData == null) shotData = shotDataSupplier.get();
		return shotData;
	}

	@Override
	public MovementData getMovementData() {
		return movement;
	}

	@Override
	public RotationData getRotationData() {
		return rotation;
	}
}
