/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability.callableentity

import java.util.concurrent.Callable

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager}

object CapabilityCallableEntityS {

  def register(): Unit = {
    val factory: Callable[_ <: DefaultCallableEntity] = () => new DefaultCallableEntity
    CapabilityManager.INSTANCE.register(
      classOf[DefaultCallableEntity],
      new Capability.IStorage[DefaultCallableEntity] {
        override def writeNBT(
            capability: Capability[DefaultCallableEntity],
            instance: DefaultCallableEntity,
            side: EnumFacing
        ): NBTBase = null

        override def readNBT(
            capability: Capability[DefaultCallableEntity],
            instance: DefaultCallableEntity,
            side: EnumFacing,
            nbt: NBTBase
        ): Unit = ()
      },
      factory
    )
  }

}
