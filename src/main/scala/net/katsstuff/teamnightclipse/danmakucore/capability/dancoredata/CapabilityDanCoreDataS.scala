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
package net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata

import java.util.concurrent.Callable

import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}

object CapabilityDanCoreDataS {

  def register(): Unit = {
    val factory: Callable[_ <: IDanmakuCoreData] = () => new BoundedDanmakuCoreData()
    CapabilityManager.INSTANCE
      .register[IDanmakuCoreData](
        classOf[IDanmakuCoreData],
        new Capability.IStorage[IDanmakuCoreData] {

          override def writeNBT(
              capability: Capability[IDanmakuCoreData],
              instance: IDanmakuCoreData,
              side: EnumFacing
          ): NBTBase = {
            val compound = new NBTTagCompound
            compound.setFloat("power", instance.getPower)
            compound.setInteger("score", instance.getScore)
            compound.setInteger("lives", instance.getLives)
            compound.setInteger("bombs", instance.getBombs)
            compound
          }

          override def readNBT(
              capability: Capability[IDanmakuCoreData],
              instance: IDanmakuCoreData,
              side: EnumFacing,
              base: NBTBase
          ): Unit = {
            val compound = base.asInstanceOf[NBTTagCompound]
            instance.setPower(compound.getFloat("power"))
            instance.setScore(compound.getInteger("score"))
            instance.setLives(compound.getInteger("lives"))
            instance.setBombs(compound.getInteger("bombs"))
          }
        },
        factory
      )
  }
}
