/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.boss;

import java.util.List;

import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

public abstract class EntityDanmakuBoss extends EntityDanmakuMob {

	//TODO: Make this like a real touhou boss bar
	private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);

	public EntityDanmakuBoss(World world) {
		super(world);
		setupPhases();
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public boolean isNonBoss() {
		return false;
	}

	private void setupPhases() {
		phaseManager.addPhases(getPhaseList());
	}

	public abstract List<Phase> getPhaseList();

	public abstract EnumTouhouCharacters getCharacter();
}
