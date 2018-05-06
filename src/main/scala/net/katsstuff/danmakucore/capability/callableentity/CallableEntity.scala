/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability.callableentity

import net.minecraft.entity.EntityLivingBase

/**
  * Entities that implement this are able to call for help. They can also help other callers.
  */
trait CallableEntity {

  /**
    * Sets the call distance
    */
  def setCallDistance(distance: Int): Unit

  /**
    * Gets the call distance
    */
  def getCallDistance: Int

  /**
    * Called when an entity within the call distance that also implements this is attacked.
    */
  def onEntityCall(caller: EntityLivingBase, target: EntityLivingBase): Unit
}
