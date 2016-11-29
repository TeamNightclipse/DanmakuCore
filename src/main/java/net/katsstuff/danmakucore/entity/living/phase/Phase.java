/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.phase;

import java.util.Optional;

import net.katsstuff.danmakucore.EnumDanmakuLevel;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.entity.spellcard.Spellcard;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Phase implements INBTSerializable<NBTTagCompound> {

	private static final String NBT_COUNTER = "counter";
	private static final String NBT_INTERVAL = "interval";
	static final String NBT_NAME = "name";

	/**
	 * The counter for this {@link Phase}. By default it is incremented each
	 * tick. If this is less than 0, it will signal that the entity using
	 * this phase is invulnerable, and provided data that can be used on the
	 * client.
	 */
	protected int counter;
	/**
	 * The set amount of time that this phase will use to reset the counter.
	 */
	protected int interval;

	/**
	 * The level to use for this {@link Phase}. Use this instead of getting
	 * it from the config as it can be changes without changing the global
	 * difficulty.
	 */
	@SuppressWarnings("unused")
	protected EnumDanmakuLevel level = ConfigHandler.danmaku.danmakuLevel;
	@SuppressWarnings("WeakerAccess")
	protected final PhaseManager manager;

	public Phase(PhaseManager manager) {
		this.manager = manager;
	}

	/**
	 * Initiate the state of this {@link Phase}
	 */
	public void init() {
		counter = 0;
		interval = 40;
	}

	@SuppressWarnings("WeakerAccess")
	public void clientUpdate() {
		counter++;
		if(counter > interval) counter = 0;
	}

	public void serverUpdate() {
		counter++;
		if(counter > interval) counter = 0;
	}

	/**
	 * Check if the counter is zero.
	 * If you want to do some action every x ticks, set
	 * the interval to x and test for this.
	 */
	protected boolean isCounterStart() {
		return counter == 0;
	}

	protected EntityDanmakuMob getEntity() {
		return manager.entity;
	}

	public abstract PhaseType getType();

	public int getCounter() {
		return counter;
	}

	/**
	 * Check if this is a spellcard. Used to get the amount of starts to show for bosses.
	 */
	public boolean isSpellcard() {
		return false;
	}

	/**
	 * Returns the name to render in spellcard like fashion.
	 * Doesn't actually need to be a real spellcard.
	 */
	public Optional<ITextComponent> getSpellcardName() {
		return Optional.empty();
	}

	/**
	 * If this {@link Phase} represents a spellcard, returns the spellcard.
	 */
	public Optional<Spellcard> getSpellcard() {
		return Optional.empty();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(NBT_NAME, getType().getFullName().toString());
		tag.setInteger(NBT_COUNTER, counter);
		tag.setInteger(NBT_INTERVAL, interval);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		counter = nbt.getInteger(NBT_COUNTER);
		interval = nbt.getInteger(NBT_INTERVAL);
	}
}