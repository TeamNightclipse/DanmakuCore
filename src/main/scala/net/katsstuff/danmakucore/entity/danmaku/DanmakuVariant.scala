/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.data.{MovementData, RotationData, ShotData, Vector3}
import net.katsstuff.danmakucore.registry.RegistryValueItemCreatable
import net.minecraft.client.renderer.block.model.{ModelResourceLocation => MRL}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.{EnumHand, ResourceLocation}

/**
  * A [[DanmakuVariant]] can be thought of as a named [[ShotData]]
  * with [[MovementData]] and [[RotationData]] packed into it.
  * It's purpose is to define what danmaku to fire, given only this and a source,
  * or a world + position + direction.
  *
  * Remember to not load the ShotData (and thus Form and SubEntity) before everything
  * there has finished to register. One approach is to make it lazy.
  */
abstract class DanmakuVariant() extends RegistryValueItemCreatable[DanmakuVariant, DanmakuTemplate] {
  def this(name: String) {
    this()
    setRegistryName(name)
    DanmakuCore.proxy.bakeDanmakuVariant(this)
  }

  def getShotData:     ShotData
  def getMovementData: MovementData
  def getRotationData: RotationData

  override def create(
      user: Option[EntityLivingBase],
      alternateMode: Boolean,
      pos: Vector3,
      direction: Vector3,
      hand: Option[EnumHand]
  ): Option[DanmakuTemplate] =
    Some(DanmakuTemplate.builder.setVariant(this).setUser(user).setDirection(direction).setPos(pos).build)

  override def getUnlocalizedName: String = "danmakuvariant." + modId + "." + name
  override def getItemModel: MRL = new MRL(new ResourceLocation(modId, "danmaku/variant/" + name), "inventory")
}
