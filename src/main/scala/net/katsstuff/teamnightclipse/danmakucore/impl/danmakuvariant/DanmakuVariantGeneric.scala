/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.danmakuvariant

import java.util.function.Supplier

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuVariant
import net.katsstuff.teamnightclipse.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._

class DanmakuVariantGeneric(
    name: String,
    shotDataSup: () => ShotData,
    movement: MovementData,
    rotation: RotationData = RotationData.none
) extends DanmakuVariant(name) {

  private lazy val shotData = shotDataSup()

  override def getShotData: ShotData         = shotData
  override def getMovementData: MovementData = movement
  override def getRotationData: RotationData = rotation
}
object DanmakuVariantGeneric {
  def apply(
      name: String,
      shotDataSup: () => ShotData,
      movement: MovementData,
      rotation: RotationData = RotationData.none
  ) = new DanmakuVariantGeneric(name, shotDataSup, movement, rotation)

  def withSpeed(name: String, shotDataSup: () => ShotData, speed: Double, rotation: RotationData = RotationData.none) =
    new DanmakuVariantGeneric(name, shotDataSup, MovementData.constant(speed), rotation)

  def create(name: String, shotDataSup: Supplier[ShotData], movement: MovementData, rotation: RotationData) =
    new DanmakuVariantGeneric(name, shotDataSup.asScala, movement, rotation)

  def create(name: String, shotDataSup: Supplier[ShotData], movement: MovementData) =
    new DanmakuVariantGeneric(name, shotDataSup.asScala, movement)

  def create(name: String, shotDataSup: Supplier[ShotData], speed: Double) =
    new DanmakuVariantGeneric(name, shotDataSup.asScala, MovementData.constant(speed))
}
