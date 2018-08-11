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
package net.katsstuff.teamnightclipse.danmakucore.entity

import net.minecraft.entity.EnumCreatureType
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

trait EntityInfo[A] {

  /**
    * Creates a new instance if this entity.
    */
  def create(world: World): A

  /**
    * The name of this entity.
    */
  def name: String

  /**
    * The tracking info for this entity.
    */
  def tracking: TrackingInfo = TrackingInfo()

  /**
    * The egg info for this entity.
    */
  def egg: Option[EggInfo] = None

  /**
    * The spawn info for this entity.
    * @return
    */
  def spawn: Option[SpawnInfo] = None
}

/**
  * Tracking into for an entity.
  * @param range The tracking range.
  * @param updateFrequency How often to send updates.
  * @param sendVelocityUpdates If velocity updates should be sent.
  */
case class TrackingInfo(range: Int = 64, updateFrequency: Int = 1, sendVelocityUpdates: Boolean = true)

/**
  * The egg color for an entity.
  */
case class EggInfo(primary: Int, secondary: Int)

/**
  * The spawn info for an entity.
  * @param creatureType What creature type this entity is.
  * @param weight The weight of of spawning for this entity.
  * @param min The minimum amount of entities to spawn at once.
  * @param max The maximum amount of entities to spawn at once.
  * @param biomes The biomes this entity can spawn in.
  */
case class SpawnInfo(creatureType: EnumCreatureType, weight: Int, min: Int, max: Int, biomes: Seq[Biome])
