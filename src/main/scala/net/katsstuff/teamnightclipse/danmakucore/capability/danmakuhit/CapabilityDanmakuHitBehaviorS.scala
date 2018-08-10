/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
