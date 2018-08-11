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
package net.katsstuff.teamnightclipse.danmakucore.danmaku

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.teamnightclipse.danmakucore.registry.RegistryValueItemCreatable
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.client.renderer.block.model.{ModelResourceLocation => MRL}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.{EnumHand, ResourceLocation}
import net.minecraft.world.World

/**
  * A [[DanmakuVariant]] can be thought of as a named [[ShotData]]
  * with [[MovementData]] and [[RotationData]] packed into it.
  * It's purpose is to define what danmaku to fire, given only this and a source,
  * or a world + position + direction.
  *
  * Remember to not load the ShotData (and thus Form and SubEntity) before everything
  * there has finished to register. One approach is to make it lazy.
  */
abstract class DanmakuVariant extends RegistryValueItemCreatable[DanmakuVariant, DanmakuTemplate] {
  def this(name: String) {
    this()
    setRegistryName(name)
    DanmakuCore.proxy.bakeDanmakuVariant(this)
  }

  def getShotData: ShotData
  def getMovementData: MovementData
  def getRotationData: RotationData

  override def create(
      world: World,
      user: Option[EntityLivingBase],
      alternateMode: Boolean,
      pos: Vector3,
      direction: Vector3,
      hand: Option[EnumHand]
  ): Option[DanmakuTemplate] =
    Some(
      DanmakuTemplate.builder.setVariant(this).setUser(user).setWorld(world).setDirection(direction).setPos(pos).build
    )

  override def unlocalizedName: String = s"danmakuvariant.$modId.$name"
  override def itemModel: MRL          = new MRL(new ResourceLocation(modId, s"danmaku/variant/$name"), "inventory")
}
object DanmakuVariant {
  implicit val ordering: Ordering[DanmakuVariant] = Ordering.by((variant: DanmakuVariant) => variant.fullNameString)
}
