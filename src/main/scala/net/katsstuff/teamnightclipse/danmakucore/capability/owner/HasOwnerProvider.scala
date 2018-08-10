/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.capability.owner
import net.katsstuff.teamnightclipse.danmakucore.entity.living.TouhouCharacter
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}

case class HasOwnerProvider(character: TouhouCharacter) extends ICapabilityProvider {
  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean =
    capability == CapabilityHasOwnerJ.HAS_OWNER
  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T =
    if (capability == CapabilityHasOwnerJ.HAS_OWNER) CapabilityHasOwnerJ.HAS_OWNER.cast(HasOwner(character))
    else CapabilityHasOwnerJ.HAS_OWNER.cast(null)
}
