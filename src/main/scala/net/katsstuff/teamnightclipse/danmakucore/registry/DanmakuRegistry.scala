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
package net.katsstuff.teamnightclipse.danmakucore.registry

import java.util.Random

import javax.annotation.Nullable

import scala.collection.JavaConverters._
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuVariant
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.Form
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.SubEntityType
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.Spellcard
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.PhaseType
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.{ForgeRegistry, IForgeRegistry}

object DanmakuRegistry {
  val Form: IForgeRegistry[Form]                     = GameRegistry.findRegistry(classOf[Form])
  val SubEntity: IForgeRegistry[SubEntityType]       = GameRegistry.findRegistry(classOf[SubEntityType])
  val DanmakuVariant: IForgeRegistry[DanmakuVariant] = GameRegistry.findRegistry(classOf[DanmakuVariant])
  val Spellcard: IForgeRegistry[Spellcard]           = GameRegistry.findRegistry(classOf[Spellcard])
  val Phase: IForgeRegistry[PhaseType]               = GameRegistry.findRegistry(classOf[PhaseType])

  def getId[T <: RegistryValue[T]](clazz: Class[T], value: T): Int =
    GameRegistry
      .findRegistry(clazz)
      .asInstanceOf[ForgeRegistry[T]]
      .getID(value)

  @Nullable
  def getObjById[T <: RegistryValue[T]](clazz: Class[T], id: Int): T =
    GameRegistry
      .findRegistry(clazz)
      .asInstanceOf[ForgeRegistry[T]]
      .getValue(id)

  def getRandomObject[T <: RegistryValue[T]](clazz: Class[T], rng: Random): T = {
    val values = GameRegistry.findRegistry(clazz).getValuesCollection.asScala.toSeq
    val idx    = rng.nextInt(values.size)
    values(idx)
  }
}
