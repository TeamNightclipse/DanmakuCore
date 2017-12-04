/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.registry

import javax.annotation.Nullable

import net.katsstuff.danmakucore.data.Vector3
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraftforge.registries.IForgeRegistryEntry

/**
  * Represents a registry value that can be instantiated.
  */
abstract class RegistryValueItemCreatable[A <: IForgeRegistryEntry[A], Obj <: AnyRef]
    extends RegistryValueWithItemModel[A] {

  /**
    * Creates an instance of this object.
    *
    * @return Some if the object can be created, None otherwise.
    */
  def create(
      user: Option[EntityLivingBase],
      alternateMode: Boolean,
      pos: Vector3,
      direction: Vector3,
      hand: Option[EnumHand]
  ): Option[Obj]

  override def canRightClick(player: EntityPlayer, hand: EnumHand): Boolean =
    create(Some(player), alternateMode = false, new Vector3(player), new Vector3(player.getLookVec), Some(hand)).isDefined

  /**
    * Creates an instance of this object.
    *
    * @return Some if the object can be created, None otherwise.
    */
  @Nullable
  def create(
      @Nullable user: EntityLivingBase,
      alternateMode: Boolean,
      pos: Vector3,
      direction: Vector3,
      @Nullable hand: EnumHand
  ): Obj = create(Option(user), alternateMode, pos, direction, Option(hand)).getOrElse(null.asInstanceOf[Obj])
}
