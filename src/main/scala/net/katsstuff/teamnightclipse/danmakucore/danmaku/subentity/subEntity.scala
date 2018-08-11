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
package net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.teamnightclipse.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.teamnightclipse.danmakucore.registry.RegistryValue
import net.katsstuff.teamnightclipse.danmakucore.misc.Translatable

/**
  * Where you define special behavior for danmaku. The most used methods ones are provided already,
  * but it's entirely possible to create new ones, and then call those methods from elsewhere.
  */
abstract class SubEntity {

  /**
    * Called when the subentity is first created. This will not be called
    * when serializing and deserializing. This will be called before [[onInstantiate]]
    */
  def onCreate(danmaku: DanmakuState): DanmakuState = danmaku

  /**
    * Always called right after [[SubEntityType.instantiate]]. Set up initial
    * state here. Remember that this isn't only called at the start of a danmaku,
    * but also when it's sent over the network in a force update.
    */
  def onInstantiate(danmaku: DanmakuState): DanmakuState = danmaku

  /**
    * Called each tick as long as the danmaku is alive, and it's delay is 0.
    */
  def subEntityTick(danmaku: DanmakuState): DanmakuUpdate

  /**
    * Callback that is executed whenever [[ShotData]] is set on the [[DanmakuState]]
    *
    * @param oldShot The old shot
    * @param formOpinion The shot that the form wants to use
    * @param newShot The new shot
    * @return The shot that will be switched to
    */
  def onShotDataChange(oldShot: ShotData, formOpinion: ShotData, newShot: ShotData): ShotData = formOpinion

  /**
    * Callback that is executed when [[MovementData]] is set on the [[DanmakuState]].
    *
    * @param oldMovement The old movement
    * @param formOpinion The movement that the form wants to use
    * @param newMovement the new movement
    * @return The movement that will be switched to
    */
  def onMovementDataChange(
      oldMovement: MovementData,
      formOpinion: MovementData,
      newMovement: MovementData
  ): MovementData = formOpinion

  /**
    * Callback that is executed when [[RotationData]] is set on the [[DanmakuState]].
    *
    * @param oldRotation The old rotation
    * @param formOpinion The rotation that the form wants to use
    * @param newRotation The new rotation
    * @return The rotation that will be switched to
    */
  def onRotationDataChange(
      oldRotation: RotationData,
      formOpinion: RotationData,
      newRotation: RotationData
  ): RotationData = formOpinion
}

abstract class SubEntityType extends RegistryValue[SubEntityType] with Translatable {

  def this(name: String) {
    this()
    setRegistryName(name)
  }

  def instantiate: SubEntity

  override def unlocalizedName: String = s"subentity.$modId.$name"
}
object SubEntityType {
  implicit val ordering: Ordering[SubEntityType] = Ordering.by((subEntity: SubEntityType) => subEntity.fullNameString)
}