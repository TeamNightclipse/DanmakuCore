/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.phase;

import net.katsstuff.danmakucore.EnumDanmakuLevel;
import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob;
import net.katsstuff.danmakucore.handler.ConfigHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class Phase implements INBTSerializable<NBTTagCompound> {

	private static final String NBT_COUNTER = "counter";
	private static final String NBT_INTERVAL = "interval";
	static final String NBT_NAME = "name";

	protected int counter;
	protected int interval;
	@SuppressWarnings("unused")
	protected EnumDanmakuLevel level = ConfigHandler.danmaku.danmakuLevel;
	protected final PhaseManager manager;

	public Phase(PhaseManager manager) {
		this.manager = manager;
	}

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

	protected EntityDanmakuMob getEntity() {
		return manager.entity;
	}

	protected abstract PhaseType getType();

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