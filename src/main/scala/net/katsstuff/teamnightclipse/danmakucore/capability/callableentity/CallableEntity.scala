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
package net.katsstuff.teamnightclipse.danmakucore.capability.callableentity

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
