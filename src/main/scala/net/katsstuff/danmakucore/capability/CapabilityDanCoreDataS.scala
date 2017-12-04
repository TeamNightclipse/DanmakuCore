/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability

import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}

object CapabilityDanCoreDataS {

  def register(): Unit =
    CapabilityManager.INSTANCE
      .register(
        classOf[IDanmakuCoreData],
        new Capability.IStorage[IDanmakuCoreData]() {

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
        () => new BoundedDanmakuCoreData
      )
}
