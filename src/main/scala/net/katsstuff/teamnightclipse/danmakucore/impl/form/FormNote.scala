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
package net.katsstuff.teamnightclipse.danmakucore.impl.form

import net.katsstuff.teamnightclipse.danmakucore.danmodel.FormDanModel
import net.katsstuff.teamnightclipse.danmakucore.data.ShotData
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanmakuHelper
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.{ResourceLocation, SoundEvent}
import net.minecraft.world.World

class FormNote(name: String, sound: SoundEvent, resource: ResourceLocation) extends FormDanModel(name, resource) {

  override def playShotSound(user: EntityLivingBase, shotData: ShotData): Unit =
    user.playSound(sound, shotData.damage / 3, 0.5F + (user.ticksExisted % 60) / 60F)

  override def playShotSound(world: World, pos: Vector3, shotData: ShotData): Unit =
    DanmakuHelper.playSoundAt(world, pos, sound, shotData.damage / 3, 0.5F + (world.getWorldTime % 60) / 40F)
}
