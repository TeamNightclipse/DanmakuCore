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
package net.katsstuff.teamnightclipse.danmakucore.capability.owner

import java.util.concurrent.Callable

import net.katsstuff.teamnightclipse.danmakucore.entity.living.TouhouCharacter
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}

object CapabilityHasOwnerS {

  def register(): Unit = {
    val factory: Callable[_ <: HasOwner] = () => HasOwner(TouhouCharacter.REIMU_HAKUREI)
    CapabilityManager.INSTANCE.register(
      classOf[HasOwner],
      new Capability.IStorage[HasOwner] {
        override def writeNBT(capability: Capability[HasOwner], instance: HasOwner, side: EnumFacing): NBTBase = null
        override def readNBT(
            capability: Capability[HasOwner],
            instance: HasOwner,
            side: EnumFacing,
            nbt: NBTBase
        ): Unit =
          ()
      },
      factory
    )
  }

}
