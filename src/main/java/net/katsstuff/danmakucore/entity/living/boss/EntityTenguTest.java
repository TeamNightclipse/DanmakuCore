/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.boss;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.katsstuff.danmakucore.data.MovementData;
import net.katsstuff.danmakucore.data.RotationData;
import net.katsstuff.danmakucore.entity.living.ai.EntityAIMoveRanged;
import net.katsstuff.danmakucore.entity.living.phase.Phase;
import net.katsstuff.danmakucore.helper.LogHelper;
import net.katsstuff.danmakucore.lib.data.LibPhases;
import net.katsstuff.danmakucore.lib.data.LibShotData;
import net.katsstuff.danmakucore.lib.data.LibSpellcards;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTenguTest extends EntityDanmakuBoss {

	public EntityTenguTest(World world) {
		super(world);
		setHealth(5F);
		setSize(1.2F, 1.3F);
		setFlyingHeight(3);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMoveRanged(this, getSpeed(), 16F));
		this.tasks.addTask(6, new EntityAIWander(this, getSpeed()));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 16F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPig.class, true));
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public List<Phase> getPhaseList() {
		ArrayList<Phase> phases = new ArrayList<>();
		phases.add(LibPhases.SHAPE_WIDE.instantiate(phaseManager, 8, 45F, 0F, 0.5D, LibShotData.SHOT_MEDIUM,
				MovementData.constant(0.4D), RotationData.none()));
		phases.add(LibPhases.SPELLCARD.instantiate(phaseManager, LibSpellcards.DELUSION_OF_ENLIGHTENMENT));
		return phases;
	}

	@Override
	public EnumTouhouCharacters getCharacter() {
		return EnumTouhouCharacters.OTHER;
	}
}
