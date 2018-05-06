/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku.subentity

import net.katsstuff.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.danmakucore.misc.Translatable
import net.katsstuff.danmakucore.registry.RegistryValue

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