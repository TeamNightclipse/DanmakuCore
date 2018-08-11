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

import net.katsstuff.teamnightclipse.danmakucore.misc.Translatable
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraftforge.registries.IForgeRegistryEntry

/**
  * Represents a registry vakue with an item model.
  */
abstract class RegistryValueWithItemModel[A <: IForgeRegistryEntry[A]] extends RegistryValue[A] with Translatable {

  /**
    * Called when a itemStack representing this is rightclicked.
    *
    * @return If the the action should continue.
    */
  def canRightClick(player: EntityPlayer, hand: EnumHand): Boolean

  def itemModel: ModelResourceLocation

}
