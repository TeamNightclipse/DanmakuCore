/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase;

import net.katsstuff.danmakucore.data.Quat;
import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.shape.IShape;
import net.katsstuff.danmakucore.shape.ShapeResult;

public class PhaseTypeShape {

	public static PhaseType create(IShape shape, boolean continuous) {
		return new PhaseType() {

			@Override
			public Phase instantiate(PhaseManager phaseManager) {
				return new PhaseShape(phaseManager, this, shape, continuous);
			}
		};
	}

	public static class PhaseShape extends Phase {

		private final PhaseType type;
		private final boolean continuous;
		private final IShape shape;

		private PhaseShape(PhaseManager manager, PhaseType type, IShape shape, boolean continuous) {
			super(manager);
			this.type = type;
			this.continuous = continuous;
			this.shape = shape;
		}

		@Override
		public void init() {
			super.init();
			if(continuous) {
				interval = 99_999;
			}
			else {
				interval = 0;
			}
		}

		@Override
		public void serverUpdate() {
			super.serverUpdate();
			if(isCounterStart()) {
				EntityDanmakuMob danmakuMob = getEntity();

				ShapeResult res = shape.draw(new Vector3(danmakuMob), Quat.orientationOf(danmakuMob), counter);

				if(continuous && res.isDone()) {
					counter = 0;
				}
			}
		}

		@Override
		public PhaseType getType() {
			return type;
		}
	}
}
