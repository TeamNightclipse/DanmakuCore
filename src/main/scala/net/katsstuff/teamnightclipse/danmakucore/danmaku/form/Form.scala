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
package net.katsstuff.teamnightclipse.danmakucore.danmaku.form

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.teamnightclipse.danmakucore.data.{MovementData, RotationData, ShotData}
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.SubEntity
import net.katsstuff.teamnightclipse.danmakucore.lib.LibSounds
import net.katsstuff.teamnightclipse.danmakucore.registry.RegistryValueWithItemModel
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanmakuHelper
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.client.renderer.block.model.{ModelResourceLocation => MRL}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EnumHand, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * Something that dictates the appearance and the logic that comes from that of a [[DanmakuState]].
  *
  * Please note that even though both this and [[SubEntity]] have callbacks
  * for tick and changes, the callbacks here should only be used if the logic is
  * specific to this form. For example size restrictions.
  */
abstract class Form extends RegistryValueWithItemModel[Form] {

  def this(name: String) {
    this()
    setRegistryName(name)
    DanmakuCore.proxy.initForm(this)
  }

  /**
    * Performs some initialization logic when the game starts on the client.
    */
  def initClient(): Unit = ()

  /**
    * The ResourceLocation assigned to this registration.
    */
  def getTexture(danmaku: DanmakuState): ResourceLocation

  /**
    * The IRenderForm assigned to this registration.
    */
  @SideOnly(Side.CLIENT)
  def getRenderer(danmaku: DanmakuState): IRenderForm

  override def canRightClick(player: EntityPlayer, hand: EnumHand) = true

  def playShotSound(user: EntityLivingBase, shotData: ShotData): Unit =
    user.playSound(LibSounds.SHOT1, 0.1F, 1F)

  def playShotSound(world: World, pos: Vector3, shotData: ShotData): Unit =
    DanmakuHelper.playSoundAt(world, pos, LibSounds.SHOT1, 0.1F, 1F)

  /**
    * Called each tick for danmaku that uses this form. While you can emulate the most of a
    * [[SubEntity]] with this, please only use it
    * for something that would always be true for the given form. For example, a wrecking ball
    * danmaku would probably always be dragged down a slight bit by gravity, even if it doesn't have normal
    * gravity.
    */
  def onTick(danmaku: DanmakuState): DanmakuUpdate = DanmakuUpdate.noUpdates(danmaku)

  /**
    * Callback that is executed whenever [[ShotData]] is set on the [[DanmakuState]]
    *
    * @param oldShot The old shot
    * @param newShot The new shot
    * @return The shot that will be switched to
    */
  def onShotDataChange(oldShot: ShotData, newShot: ShotData): ShotData = newShot

  /**
    * Callback that is executed when [[MovementData]] is set on the [[DanmakuState]].
    *
    * @param oldMovement The old movement
    * @param newMovement the new movement
    * @return The movement that will be switched to
    */
  def onMovementDataChange(oldMovement: MovementData, newMovement: MovementData): MovementData = newMovement

  /**
    * Callback that is executed when [[RotationData]] is set on the [[DanmakuState]].
    *
    * @param oldRotation The old rotation
    * @param newRotation The new rotation
    * @return The rotation that will be switched to
    */
  def onRotationDataChange(oldRotation: RotationData, newRotation: RotationData): RotationData = newRotation

  override def unlocalizedName: String = s"form.$modId.$name"

  override def itemModel: MRL = new MRL(new ResourceLocation(modId, s"danmaku/form/$name"), "inventory")
}
object Form {
  implicit val ordering: Ordering[Form] = Ordering.by((form: Form) => form.fullNameString)
}
