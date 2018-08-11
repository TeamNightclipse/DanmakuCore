/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.helper

import java.util.{Optional, UUID}

import scala.collection.JavaConverters._

import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.teamnightclipse.mirror.data.{AbstractVector3, Vector3}
import net.minecraft.entity.Entity
import net.minecraft.nbt.{NBTTagCompound, NBTTagDouble, NBTTagList}
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

object NBTHelper {

  def setVector(tag: NBTTagCompound, tagName: String, vector: AbstractVector3): NBTTagCompound = {
    val list = new NBTTagList
    list.appendTag(new NBTTagDouble(vector.x))
    list.appendTag(new NBTTagDouble(vector.y))
    list.appendTag(new NBTTagDouble(vector.z))
    tag.setTag(tagName, list)
    tag
  }

  def getVector(tag: NBTTagCompound, tagName: String): Vector3 = {
    val list = tag.getTagList(tagName, Constants.NBT.TAG_DOUBLE)
    new Vector3(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2))
  }

  def getEntityByUUID(uuid: UUID, world: World): Optional[Entity] =
    world.loadedEntityList.asScala.find(_.getUniqueID == uuid).toOptional
}