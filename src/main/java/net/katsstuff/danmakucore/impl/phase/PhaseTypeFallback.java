/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase;

import net.katsstuff.danmakucore.data.Vector3;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuBuilder;
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.entity.living.phase.PhaseManager;
import net.katsstuff.danmakucore.entity.living.phase.PhaseType;
import net.katsstuff.danmakucore.helper.DanmakuCreationHelper;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;

public class PhaseTypeFallback extends PhaseType {

	@Override
	public Phase instantiate(PhaseManager phaseManager) {
		return new PhaseFallback(phaseManager, this);
	}

	public static class PhaseFallback extends Phase {

		private final PhaseTypeFallback type;
		private final DanmakuVariant variant = DanmakuRegistry.DANMAKU_VARIANT.getRandomObject(getEntity().getRNG());
		private final int amount = getEntity().getRNG().nextInt(8);

		public PhaseFallback(PhaseManager manager, PhaseTypeFallback type) {
			super(manager);
			this.type = type;
		}

		@Override
		public void serverUpdate() {
			super.serverUpdate();
			if(isCounterStart()) {
				EntityDanmakuMob user = getEntity();
				LogHelper.warn("This is the fallback phase being used for" + user + ". If you are seeing this something broke");
				Vector3 pos = user.pos();
				Vector3 angle = Vector3.angleEntity(user);
				if(!variant.onShootDanmaku(user, false, pos, angle)) return;
				DanmakuBuilder.Builder builder = variant.toBuilder();
				DanmakuCreationHelper.createCircleShot(builder.setUser(user).build(), amount, 0F, 0.2D);
			}
		}

		@Override
		protected PhaseType getType() {
			return type;
		}
	}
}
