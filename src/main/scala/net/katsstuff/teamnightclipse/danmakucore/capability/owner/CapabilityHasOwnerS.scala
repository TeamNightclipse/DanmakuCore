/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
