/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityDanmakuCoreData {

	@CapabilityInject(IDanmakuCoreData.class)
	public static Capability<IDanmakuCoreData> DANMAKUCORE_DATA_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IDanmakuCoreData.class, new Capability.IStorage<IDanmakuCoreData>() {

			@Override
			public NBTBase writeNBT(Capability<IDanmakuCoreData> capability, IDanmakuCoreData instance, EnumFacing side) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setFloat("power", instance.getPower());
				compound.setInteger("score", instance.getScore());
				compound.setInteger("lives", instance.getLives());
				compound.setInteger("bombs", instance.getBombs());
				return compound;
			}

			@Override
			public void readNBT(Capability<IDanmakuCoreData> capability, IDanmakuCoreData instance, EnumFacing side, NBTBase base) {
				NBTTagCompound compound = (NBTTagCompound)base;
				instance.setPower(compound.getFloat("power"));
				instance.setScore(compound.getInteger("score"));
				instance.setLives(compound.getInteger("lives"));
				instance.setBombs(compound.getInteger("bombs"));
			}
		}, BoundedDanmakuCoreData::new);
	}
}
