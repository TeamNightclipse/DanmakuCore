/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity

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
