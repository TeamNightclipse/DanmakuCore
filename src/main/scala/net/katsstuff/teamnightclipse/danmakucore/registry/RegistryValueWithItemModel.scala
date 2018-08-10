/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.registry
import net.katsstuff.teamnightclipse.danmakucore.misc.Translatable
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraftforge.registries.IForgeRegistryEntry

/**
  * Represents a registry vakue with an item model.
  */
abstract class RegistryValueWithItemModel[A <: IForgeRegistryEntry[A]] extends RegistryValue[A] with Translatable {

  /**
    * Called when a itemStack representing this is rightclicked.
    *
    * @return If the the action should continue.
    */
  def canRightClick(player: EntityPlayer, hand: EnumHand): Boolean

  def itemModel: ModelResourceLocation

}
