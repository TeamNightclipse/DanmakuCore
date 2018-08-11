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
package net.katsstuff.teamnightclipse.danmakucore.capability.danmakuhit

import java.util.concurrent.Callable

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}

object CapabilityDanmakuHitBehaviorS {

  def register(): Unit = {
    val factory: Callable[_ <: DanmakuHitBehavior] = () => DefaultHitBehavior
    CapabilityManager.INSTANCE
      .register[DanmakuHitBehavior](
        classOf[DanmakuHitBehavior],
        new Capability.IStorage[DanmakuHitBehavior] {

          override def writeNBT(
              capability: Capability[DanmakuHitBehavior],
              instance: DanmakuHitBehavior,
              side: EnumFacing
          ): NBTBase = null

          override def readNBT(
              capability: Capability[DanmakuHitBehavior],
              instance: DanmakuHitBehavior,
              side: EnumFacing,
              base: NBTBase
          ): Unit = {}
        },
        factory
      )

  }
}
