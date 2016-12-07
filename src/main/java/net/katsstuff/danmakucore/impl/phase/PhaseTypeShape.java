/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase;

import java.util.Set;

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.shape.IShape;
import net.minecraft.util.Tuple;

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
				interval = 99999;
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

				Tuple<Boolean, Set<EntityDanmaku>> done = shape.drawForTick(new Vector3(danmakuMob), Vector3.angleEntity(danmakuMob), counter);

				if(continuous && done.getFirst()) {
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
