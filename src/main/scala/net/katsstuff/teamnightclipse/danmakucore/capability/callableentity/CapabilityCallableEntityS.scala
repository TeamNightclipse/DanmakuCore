/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.capability.callableentity

import java.util.concurrent.Callable

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}

object CapabilityCallableEntityS {

  def register(): Unit = {
    val factory: Callable[_ <: CallableEntity] = () => new DefaultCallableEntity
    CapabilityManager.INSTANCE.register(
      classOf[CallableEntity],
      new Capability.IStorage[CallableEntity] {
        override def writeNBT(
            capability: Capability[CallableEntity],
            instance: CallableEntity,
            side: EnumFacing
        ): NBTBase = null

        override def readNBT(
            capability: Capability[CallableEntity],
            instance: CallableEntity,
            side: EnumFacing,
            nbt: NBTBase
        ): Unit = ()
      },
      factory
    )
  }

}
