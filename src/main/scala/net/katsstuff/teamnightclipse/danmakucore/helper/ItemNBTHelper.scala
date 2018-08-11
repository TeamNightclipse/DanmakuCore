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

import java.util.UUID

import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTBase, NBTTagCompound, NBTTagList}
import net.minecraftforge.common.util.Constants

object ItemNBTHelper {

  def getNBT(stack: ItemStack): NBTTagCompound = {
    if (stack.hasTagCompound) stack.getTagCompound
    else {
      val nbt = new NBTTagCompound
      stack.setTagCompound(nbt)
      nbt
    }
  }

  def setTag(stack: ItemStack, key: String, tag: NBTBase): Unit             = getNBT(stack).setTag(key, tag)
  def setBoolean(stack: ItemStack, key: String, b: Boolean): Unit           = getNBT(stack).setBoolean(key, b)
  def setByte(stack: ItemStack, key: String, b: Byte): Unit                 = getNBT(stack).setByte(key, b)
  def setShort(stack: ItemStack, key: String, s: Short): Unit               = getNBT(stack).setShort(key, s)
  def setInt(stack: ItemStack, key: String, i: Int): Unit                   = getNBT(stack).setInteger(key, i)
  def setLong(stack: ItemStack, key: String, l: Long): Unit                 = getNBT(stack).setLong(key, l)
  def setFloat(stack: ItemStack, key: String, f: Float): Unit               = getNBT(stack).setFloat(key, f)
  def setDouble(stack: ItemStack, key: String, d: Double): Unit             = getNBT(stack).setDouble(key, d)
  def setString(stack: ItemStack, key: String, s: String): Unit             = getNBT(stack).setString(key, s)
  def setByteArray(stack: ItemStack, key: String, arr: Array[Byte]): Unit   = getNBT(stack).setByteArray(key, arr)
  def setIntArray(stack: ItemStack, key: String, arr: Array[Int]): Unit     = getNBT(stack).setIntArray(key, arr)
  def setCompound(stack: ItemStack, key: String, nbt: NBTTagCompound): Unit = setTag(stack, key, nbt)
  def setList(stack: ItemStack, key: String, nbt: NBTTagList): Unit         = setTag(stack, key, nbt)
  def setUUID(stack: ItemStack, key: String, uuid: UUID): Unit              = getNBT(stack).setUniqueId(key, uuid)

  def getBoolean(stack: ItemStack, key: String): Boolean           = getNBT(stack).getBoolean(key)
  def getByte(stack: ItemStack, key: String): Byte                 = getNBT(stack).getByte(key)
  def getShort(stack: ItemStack, key: String): Short               = getNBT(stack).getShort(key)
  def getInt(stack: ItemStack, key: String): Int                   = getNBT(stack).getInteger(key)
  def getLong(stack: ItemStack, key: String): Long                 = getNBT(stack).getLong(key)
  def getFloat(stack: ItemStack, key: String): Float               = getNBT(stack).getFloat(key)
  def getDouble(stack: ItemStack, key: String): Double             = getNBT(stack).getDouble(key)
  def getString(stack: ItemStack, key: String): String             = getNBT(stack).getString(key)
  def getByteArray(stack: ItemStack, key: String): Array[Byte]     = getNBT(stack).getByteArray(key)
  def getIntArray(stack: ItemStack, key: String): Array[Int]       = getNBT(stack).getIntArray(key)
  def getCompound(stack: ItemStack, key: String): NBTTagCompound   = getNBT(stack).getCompoundTag(key)
  def getList(stack: ItemStack, key: String, tpe: Int): NBTTagList = getNBT(stack).getTagList(key, tpe)
  def getUUID(stack: ItemStack, key: String): UUID                 = getNBT(stack).getUniqueId(key)

  def hasTag(stack: ItemStack, key: String, tpe: Int): Boolean = getNBT(stack).hasKey(key, tpe)

  def getBooleanOrDefault(stack: ItemStack, key: String, default: Boolean): Boolean =
    if (hasTag(stack, key, Constants.NBT.TAG_BYTE)) getBoolean(stack, key) else default
  def getByteOrDefault(stack: ItemStack, key: String, default: Byte): Byte =
    if (hasTag(stack, key, Constants.NBT.TAG_BYTE)) getByte(stack, key) else default
  def getShortOrDefault(stack: ItemStack, key: String, default: Short): Short =
    if (hasTag(stack, key, Constants.NBT.TAG_SHORT)) getShort(stack, key) else default
  def getIntOrDefault(stack: ItemStack, key: String, default: Int): Int =
    if (hasTag(stack, key, Constants.NBT.TAG_INT)) getInt(stack, key) else default
  def getLongOrDefault(stack: ItemStack, key: String, default: Long): Long =
    if (hasTag(stack, key, Constants.NBT.TAG_LONG)) getLong(stack, key) else default
  def getFloatOrDefault(stack: ItemStack, key: String, default: Float): Float =
    if (hasTag(stack, key, Constants.NBT.TAG_FLOAT)) getFloat(stack, key) else default
  def getDoubleOrDefault(stack: ItemStack, key: String, default: Double): Double =
    if (hasTag(stack, key, Constants.NBT.TAG_DOUBLE)) getDouble(stack, key) else default
  def getStringOrDefault(stack: ItemStack, key: String, default: String): String =
    if (hasTag(stack, key, Constants.NBT.TAG_STRING)) getString(stack, key) else default
  def getByteArrayOrDefault(stack: ItemStack, key: String, default: Array[Byte]): Array[Byte] =
    if (hasTag(stack, key, Constants.NBT.TAG_BYTE_ARRAY)) getByteArray(stack, key) else default
  def getIntArrayOrDefault(stack: ItemStack, key: String, default: Array[Int]): Array[Int] =
    if (hasTag(stack, key, Constants.NBT.TAG_INT_ARRAY)) getIntArray(stack, key) else default
  def getCompoundOrDefault(stack: ItemStack, key: String, default: NBTTagCompound): NBTTagCompound =
    if (hasTag(stack, key, Constants.NBT.TAG_COMPOUND)) getCompound(stack, key) else default
  def getListOrDefault(stack: ItemStack, key: String, default: NBTTagList, tpe: Int): NBTTagList =
    if (hasTag(stack, key, Constants.NBT.TAG_LIST)) getList(stack, key, tpe) else default
  def getUUIDOrDefault(stack: ItemStack, key: String, default: UUID): UUID =
    if (getNBT(stack).hasUniqueId(key)) getUUID(stack, key) else default
}
