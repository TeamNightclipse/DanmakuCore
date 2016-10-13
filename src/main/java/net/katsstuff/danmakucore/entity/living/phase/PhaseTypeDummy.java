/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.phase;

public class PhaseTypeDummy extends PhaseType {

	@Override
	public Phase instantiate(PhaseManager phaseManager) {
		return new PhaseDummy(phaseManager, this);
	}

	private static class PhaseDummy extends Phase {

		private final PhaseTypeDummy type;

		PhaseDummy(PhaseManager manager, PhaseTypeDummy type) {
			super(manager);
			this.type = type;
		}

		@Override
		protected PhaseType getType() {
			return type;
		}
	}
}
