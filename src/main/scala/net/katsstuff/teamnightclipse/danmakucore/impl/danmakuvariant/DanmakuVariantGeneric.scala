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
