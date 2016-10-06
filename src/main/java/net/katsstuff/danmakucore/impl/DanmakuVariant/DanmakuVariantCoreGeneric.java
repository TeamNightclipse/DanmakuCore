/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.DanmakuVariant;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.lib.LibMod;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;

public class DanmakuVariantCoreGeneric extends DanmakuVariant {

	private final ShotData shotData;
	private final MovementData movement;
	private final RotationData rotation = RotationData.none();
	private final String name;

	private DanmakuVariantCoreGeneric(String name, ShotData shotData, MovementData movement) {
		setRegistryName(name);
		DanmakuRegistry.INSTANCE.danmakuVariant.register(this);
		this.shotData = shotData;
		this.movement = movement;
		this.name = name;
	}

	public DanmakuVariantCoreGeneric(String name, ShotData shotData, double speed) {
		this(name, shotData, new MovementData(speed, speed, 0.0D, Vector3.GravityZero()));
	}

	@Override
	public ShotData getShotData() {
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

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName() + "." + LibMod.MODID +  "." + name;
	}
}
