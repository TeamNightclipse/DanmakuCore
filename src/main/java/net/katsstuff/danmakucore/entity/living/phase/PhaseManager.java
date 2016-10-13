/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.registry.DanmakuRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class PhaseManager implements INBTSerializable<NBTTagCompound> {

	private static final String NBT_PHASES = "phases";

	private final List<Phase> phaseList = new ArrayList<>();
	private int currentPhaseIndex = 0;

	public final EntityDanmakuMob entity;

	public PhaseManager(EntityDanmakuMob entity) {
		this.entity = entity;
	}

	/**
	 * Ticks the current phase.
	 */
	public void tick() {
		Phase phase = getCurrentPhase();
		if(!entity.worldObj.isRemote) {
			phase.serverUpdate();
		}
		else {
			phase.clientUpdate();
		}
	}

	/**
	 * The phase that us currently in use.
	 */
	public Phase getCurrentPhase() {
		if(phaseList.get(currentPhaseIndex) == null) {
			throw new IndexOutOfBoundsException(entity.getName() + " tried to execute a non-existent attack");
		}
		return phaseList.get(currentPhaseIndex);
	}

	/**
	 * Sets a phase as the one in use, overriding the previous one. This also initiates the new phase.
	 */
	public void setCurrentPhase(Phase phase) {
		phaseList.set(currentPhaseIndex, phase);
		phase.init();
	}

	/**
	 * Sets the current phase to the next one and initiates it.
	 */
	public void nextPhase() {
		currentPhaseIndex++;
		getCurrentPhase().init();
	}

	/**
	 * Sets the current phase to the previous one and initiates it.
	 */
	public void previousPhase() {
		currentPhaseIndex--;
		getCurrentPhase().init();
	}

	/**
	 * Adds a new phase.
	 */
	public void addPhase(Phase phase) {
		phaseList.add(phase);
	}

	/**
	 * Adds a new phase at a specific index.
	 */
	public void addPhase(Phase phase, int index) {
		phaseList.add(index, phase);
	}


	/**
	 * Adds new phases.
	 */
	public void addPhases(List<Phase> phases) {
		phaseList.addAll(phases);
	}

	/**
	 * Adds new phases.
	 */
	public void addPhases(Phase... phases) {
		Collections.addAll(phaseList, phases);
	}

	/**
	 * Sets a phase at the specific index.
	 */
	public void setPhase(Phase phase, int index) {
		phaseList.set(index, phase);
	}

	/**
	 * Removes a phase.
	 */
	public void removePhase(Phase phase) {
		phaseList.remove(phase);
	}

	/**
	 * Removes a phase.
	 */
	public void removePhase(int index) {
		phaseList.remove(index);
	}

	/**
	 * Gets the index of a phase.
	 */
	public int getPhaseIndex(Phase phase) {
		return phaseList.indexOf(phase);
	}

	/**
	 * Changes the current active phase without throwing away the previous one.
	 */
	public void changePhase(int newPhase) {
		currentPhaseIndex = newPhase;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for(Phase phase : phaseList) {
			list.appendTag(phase.serializeNBT());
		}
		tag.setTag(NBT_PHASES, list);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		NBTTagList list = tag.getTagList(NBT_PHASES, Constants.NBT.TAG_COMPOUND);
		int size = list.tagCount();
		for(int i = 0; i < size; i++) {
			NBTTagCompound tagPhase = list.getCompoundTagAt(i);
			PhaseType type = DanmakuRegistry.PHASE.getObject(new ResourceLocation(tagPhase.getString(Phase.NBT_NAME)));
			Phase phase = type.instantiate(this);
			phase.deserializeNBT(tagPhase);
			phaseList.add(phase);
		}
	}
}
