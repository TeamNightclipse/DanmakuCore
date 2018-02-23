/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper

import java.util.{Optional, UUID}

import scala.collection.JavaConverters._

import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.mirror.data.{AbstractVector3, Vector3}
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