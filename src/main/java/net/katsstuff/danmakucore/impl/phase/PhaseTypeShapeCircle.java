/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.data.ShotData;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.impl.shape.ShapeCircle;
import net.minecraft.nbt.NBTTagCompound;

public class PhaseTypeShapeCircle extends PhaseType {

	@Override
	public Phase instantiate(PhaseManager phaseManager) {
		return new Circle(this, phaseManager, 8, 0F, 0.5D, ShotData.DefaultShotData(), MovementData.constant(0.4D), RotationData.none());
	}

	public Phase instantiate(PhaseManager phaseManager, int amount, float baseAngle, double distance, ShotData shotData,
			MovementData movementData, RotationData rotationData) {
		return new Circle(this, phaseManager, amount, baseAngle, distance, shotData, movementData, rotationData);
	}

	public static class Circle extends PhaseShapeConcrete {

		private final PhaseTypeShapeCircle type;

		public Circle(PhaseTypeShapeCircle type, PhaseManager manager, int amount, float baseAngle, double distance, ShotData shotData,
				MovementData movementData, RotationData rotationData) {
			super(manager, amount, baseAngle, distance, shotData, movementData, rotationData);
			this.type = type;

			DanmakuTemplate template = DanmakuTemplate.builder()
					.setUser(getEntity())
					.setShot(shotData)
					.setMovementData(movementData)
					.setRotationData(rotationData)
					.build();
			shape = new ShapeCircle(template, amount, baseAngle, distance);
		}

		@Override
		public PhaseType getType() {
			return type;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			super.deserializeNBT(tag);

			DanmakuTemplate template = DanmakuTemplate.builder()
					.setUser(getEntity())
					.setShot(shotData)
					.setMovementData(movementData)
					.setRotationData(rotationData)
					.build();
			shape = new ShapeCircle(template, amount, baseAngle, distance);
		}
	}
}
