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
import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
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
  val RL: ResourceLocation                     = DanmakuCore.resource("DanmakuHitBehavior")

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
