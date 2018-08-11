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
package net.katsstuff.teamnightclipse.danmakucore.registry

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

abstract class RegistryValue[A <: IForgeRegistryEntry[A]]
    extends IForgeRegistryEntry.Impl[A]
    with Comparable[RegistryValue[A]] {

  /**
    * The full name as of this value. Both modId and name.
    */
  def fullName: ResourceLocation = getRegistryName
  def fullNameString: String     = fullName.toString

  /**
    * Get the mod id for this value.
    */
  def modId: String = fullName.getNamespace

  /**
    * Get the short name for this value.
    */
  def name: String = fullName.getPath

  override def compareTo(other: RegistryValue[A]): Int =
    fullNameString.compareToIgnoreCase(other.getRegistryName.toString)

  override def equals(obj: Any): Boolean = obj match {
    case that: RegistryValueWithItemModel[_] => fullName == that.fullName
    case _                                   => false
  }

  override def hashCode: Int = fullName.hashCode
}
