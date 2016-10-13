/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

@SuppressWarnings("unused")
public class NBTHelper {

	public static final String NBT_VECTOR = "vector";

	public static NBTTagCompound setVector(NBTTagCompound tag, String tagName, Vec3d vector) {
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagDouble(vector.xCoord));
		list.appendTag(new NBTTagDouble(vector.yCoord));
		list.appendTag(new NBTTagDouble(vector.zCoord));
		tag.setTag(tagName, list);
		return tag;
	}

	public static Vec3d getVector(NBTTagCompound tag, String tagName) {
		NBTTagList list = tag.getTagList(tagName, Constants.NBT.TAG_DOUBLE);
		return new Vec3d(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2));
	}

	public static Optional<Entity> getEntityByUUID(UUID uuid, World world) {
		List<Entity> entityList = world.loadedEntityList;

		for(Entity entity : entityList) {
			if(entity.getUniqueID().equals(uuid)) return Optional.of(entity);
		}

		return Optional.empty();
	}
}
