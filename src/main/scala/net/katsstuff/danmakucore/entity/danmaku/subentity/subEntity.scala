/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.subentity

import net.katsstuff.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.misc.Translatable
import net.katsstuff.danmakucore.registry.RegistryValue
import net.minecraft.world.World

/**
  * Where you define special behavior for danmaku. The most used methods ones are provided already,
  * but it's entirely possible to create new ones, and then call those methods from elsewhere.
  */
abstract class SubEntity(val world: World, val danmaku: EntityDanmaku) {

  /**
    * Called each tick as long as the danmaku is alive, and it's delay is 0.
    */
  def subEntityTick(): Unit

  /**
    * Callback that is executed whenever [[ShotData]] is set on the underlying entity
    *
    * @param oldShot The old shot
    * @param formOpinion The shot that the form wants to use
    * @param newShot The new shot
    * @return The shot that will be switched to
    */
  def onShotDataChange(oldShot: ShotData, formOpinion: ShotData, newShot: ShotData): ShotData = formOpinion

  /**
    * Callback that is executed when [[MovementData]] is set on the underlying entity.
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
    * Callback that is executed when [[RotationData]] is set on the underlying entity.
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

  def instantiate(world: World, entityDanmaku: EntityDanmaku): SubEntity

  override def getUnlocalizedName: String = "subentity." + modId + "." + name
}
