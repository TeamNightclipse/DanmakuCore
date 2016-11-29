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
import java.util.UUID;

import com.google.common.base.Optional;

import net.katsstuff.danmakucore.DanmakuCore;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.misc.LogicalSideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public abstract class EntityDanmakuBoss extends EntityDanmakuMob {

	private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);

	private static final DataParameter<Optional<UUID>> BOSS_INFO_UUID = EntityDataManager.createKey(EntityDanmakuBoss.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);

	public EntityDanmakuBoss(World world) {
		super(world);
		setupPhases();
		DanmakuCore.proxy.addDanmakuBoss(this);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(BOSS_INFO_UUID, Optional.absent());
	}

	public UUID getBossInfoUUID() {
		return dataManager.get(BOSS_INFO_UUID).or(new UUID(0L, 0L));
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	@Override
	public void onUpdate() {
		if(firstUpdate && !worldObj.isRemote) {
			updateBossName();
		}

		super.onUpdate();
	}

	@SuppressWarnings("Guava")
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!worldObj.isRemote) {
			dataManager.set(BOSS_INFO_UUID, Optional.of(bossInfo.getUniqueId()));
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		DanmakuCore.proxy.removeDanmakuBoss(this);
	}

	@Override
	protected void onDeathUpdate() {
		if(phaseManager.hasNextPhase()) {
			phaseManager.nextPhase();
			if(!worldObj.isRemote) {
				updateBossName();
				setHealth(getMaxHealth());
			}
			isDead = false;
			deathTime = 0;
		}
		else {
			super.onDeathUpdate();
		}
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return phaseManager.getCurrentPhase().getCounter() < 0 || super.isEntityInvulnerable(source);
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

	@SuppressWarnings("WeakerAccess")
	@LogicalSideOnly(Side.SERVER)
	protected void updateBossName() {
		java.util.Optional<ITextComponent> spellcardName = phaseManager.getCurrentPhase().getSpellcardName();
		spellcardName.ifPresent(iTextComponent -> bossInfo.setName(this.getDisplayName().appendText(" ").appendSibling(iTextComponent)));
	}

	public int remainingSpellcards() {
		return (int)phaseManager.getPhaseList().stream().skip(phaseManager.getCurrentPhaseIndex() + 1).filter(Phase::isSpellcard).count();
	}

	public abstract List<Phase> getPhaseList();

	@SuppressWarnings("unused")
	public abstract EnumTouhouCharacters getCharacter();
}
