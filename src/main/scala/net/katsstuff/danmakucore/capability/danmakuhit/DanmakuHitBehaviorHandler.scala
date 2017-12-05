/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability.danmakuhit

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.minecraft.entity.{Entity, EntityAgeable, EntityLivingBase, MultiPartEntityPart}
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class DanmakuHitBehaviorProvider(val behavior: DanmakuHitBehavior) extends ICapabilityProvider {
  val Behavior: Capability[DanmakuHitBehavior] = CapabilityDanmakuHitBehaviorJ.HIT_BEHAVIOR

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T =
    if (capability == Behavior) Behavior.cast(behavior) else Behavior.cast(null)
  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = capability == Behavior
}
object DanmakuHitBehaviorHandler {

  val Behavior: Capability[DanmakuHitBehavior] = CapabilityDanmakuHitBehaviorJ.HIT_BEHAVIOR
  val RL:       ResourceLocation               = DanmakuCore.resource("DanmakuHitBehavior")

  @SubscribeEvent
  def attachEntity(event: AttachCapabilitiesEvent[Entity]): Unit = {
    val toAttach = event.getObject match {
      case _: EntityAgeable                             => IgnoreHitBehavior
      case _: EntityLivingBase | _: MultiPartEntityPart => DefaultHitBehavior
      case _                                            => IgnoreHitBehavior
    }

    event.addCapability(RL, new DanmakuHitBehaviorProvider(toAttach))
  }
}
