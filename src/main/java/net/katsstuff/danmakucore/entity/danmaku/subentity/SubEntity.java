/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.subentity;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.minecraft.world.World;

/**
 * Where you define special behavior for danmaku. The most used methods ones are provided already,
 * but it's entirely possible to create new ones, and then call those methods from elsewhere.
 */
public abstract class SubEntity {

	protected final World world;
	protected final EntityDanmaku danmaku;

	public SubEntity(World world, EntityDanmaku danmaku) {
		this.world = world;
		this.danmaku = danmaku;
	}

	/**
	 * Called each tick as long as the danmaku is alive, and it's delay is 0.
	 */
	public abstract void subEntityTick();

	/**
	 * Callback that is executed whenever {@link ShotData} is set on the underlying entity
	 * @param oldShot The old shot
	 * @param formOpinion The shot that the form wants to use
	 * @param newShot The new shot
	 * @return The shot that will be switched to
	 */
	public ShotData onShotDataChange(ShotData oldShot, ShotData formOpinion, ShotData newShot) {
		return formOpinion;
	}

	/**
	 * Callback that is executed when {@link MovementData} is set on the underlying entity.
	 * @param oldMovement The old movement
	 * @param formOpinion The movement that the form wants to use
	 * @param newMovement the new movement
	 * @return The movement that will be switched to
	 */
	public MovementData onMovementDataChange(MovementData oldMovement, MovementData formOpinion, MovementData newMovement) {
		return formOpinion;
	}

	/**
	 * Callback that is executed when {@link RotationData} is set on the underlying entity.
	 * @param oldRotation The old rotation
	 * @param formOpinion The rotation that the form wants to use
	 * @param newRotation The new rotation
	 * @return The rotation that will be switched to
	 */
	public RotationData onRotationDataChange(RotationData oldRotation, RotationData formOpinion, RotationData newRotation) {
		return formOpinion;
	}

}
