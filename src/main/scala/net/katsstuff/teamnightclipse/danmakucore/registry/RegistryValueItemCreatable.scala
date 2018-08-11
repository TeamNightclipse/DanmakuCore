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

import javax.annotation.Nullable

import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.world.World
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
      world: World,
      user: Option[EntityLivingBase],
      alternateMode: Boolean,
      pos: Vector3,
      direction: Vector3,
      hand: Option[EnumHand]
  ): Option[Obj]

  override def canRightClick(player: EntityPlayer, hand: EnumHand): Boolean =
    create(
      player.world,
      Some(player),
      alternateMode = false,
      new Vector3(player),
      new Vector3(player.getLookVec),
      Some(hand)
    ).isDefined

  /**
    * Creates an instance of this object.
    *
    * @return Some if the object can be created, None otherwise.
    */
  @Nullable
  def create(
      world: World,
      @Nullable user: EntityLivingBase,
      alternateMode: Boolean,
      pos: Vector3,
      direction: Vector3,
      @Nullable hand: EnumHand
  ): Obj = create(world, Option(user), alternateMode, pos, direction, Option(hand)).getOrElse(null.asInstanceOf[Obj])
}
