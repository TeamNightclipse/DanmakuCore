/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

abstract class RegistryValue[A <: IForgeRegistryEntry[A]]
    extends IForgeRegistryEntry.Impl[A]
    with Comparable[RegistryValue[A]] {

  /**
    * The full name as of this value. Both modId and name.
    */
  def fullName:       ResourceLocation = getRegistryName
  def fullNameString: String           = fullName.toString

  /**
    * Get the mod id for this value.
    */
  def modId: String = fullName.getResourceDomain

  /**
    * Get the short name for this value.
    */
  def name: String = fullName.getResourcePath

  override def compareTo(other: RegistryValue[A]): Int =
    fullNameString.compareToIgnoreCase(other.getRegistryName.toString)

  override def equals(obj: Any): Boolean = obj match {
    case that: RegistryValueWithItemModel[_] => fullName == that.fullName
    case _                                   => false
  }

  override def hashCode: Int = fullName.hashCode
}
