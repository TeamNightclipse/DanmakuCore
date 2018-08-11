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
package net.katsstuff.teamnightclipse.danmakucore.helper

import net.katsstuff.teamnightclipse.danmakucore.entity.{EntityFallingData, FallingDataTypes}
import net.katsstuff.teamnightclipse.danmakucore.lib.LibSounds
import net.katsstuff.teamnightclipse.danmakucore.network.{ChargeSpherePacket, DanCorePacketHandler}
import net.katsstuff.teamnightclipse.mirror.client.particles.{GlowTexture, ParticleUtil}
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.TargetPoint
import net.minecraft.entity.Entity
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait TTouhouHelper {

  /**
    * Offsets a vector in a random direction by one.
    *
    * @param pos The vector to offset
    * @return The new vector
    */
  def fuzzPosition(pos: Vector3): Vector3 = fuzzPosition(pos, 1D)

  /**
    * Adds some random offset to a vector.
    *
    * @param pos The vector to offset
    * @param amount The amount to offset
    * @return The new vector
    */
  def fuzzPosition(pos: Vector3, amount: Double): Vector3 = pos.offset(Vector3.randomVector, amount)

  /**
    * Creates a power entity.
    *
    * @param world The world
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createPower(world: World, pos: Vector3, direction: Vector3): EntityFallingData =
    new EntityFallingData(
      world,
      FallingDataTypes.power,
      fuzzPosition(pos),
      Vector3.limitRandomDirection(direction, 7.5F),
      None,
      0.05F
    )

  /**
    * Creates a big power entity.
    *
    * @param world The world
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createBigPower(world: World, pos: Vector3, direction: Vector3): EntityFallingData =
    new EntityFallingData(
      world,
      FallingDataTypes.bigPower,
      fuzzPosition(pos),
      Vector3.limitRandomDirection(direction, 7.5F),
      None,
      1F
    )

  /**
    * Creates a life entity.
    *
    * @param world The world
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createLife(world: World, pos: Vector3, direction: Vector3): EntityFallingData =
    new EntityFallingData(
      world,
      FallingDataTypes.life,
      fuzzPosition(pos),
      Vector3.limitRandomDirection(direction, 7.5F),
      None,
      1
    )

  /**
    * Creates a bomb entity.
    *
    * @param world The world
    * @param pos The position for the entity. Will be fuzzed.
    * @param direction The direction that the entity will go in.
    * @return The score entity
    */
  def createBomb(world: World, pos: Vector3, direction: Vector3): EntityFallingData =
    new EntityFallingData(
      world,
      FallingDataTypes.bomb,
      fuzzPosition(pos),
      Vector3.limitRandomDirection(direction, 7.5F),
      None,
      1
    )

  /**
    * Creates a charge circle at the center of the specified entity.
    * You might have to play with some of the parameters to make it look cool.
    *
    * @param entity The entity at the center
    * @param amount The amount of particles to spawn
    * @param offset The amount of offset where the particles will start
    * @param divSpeed The amount to divide the initial speed with
    * @param r The red component
    * @param g The green component
    * @param b The blue component
    * @param lifetime The lifetime of the particles
    */
  @SideOnly(Side.CLIENT)
  def createChargeSphere(
      entity: Entity,
      amount: Int,
      offset: Double,
      divSpeed: Double,
      r: Float,
      g: Float,
      b: Float,
      lifetime: Int
  ): Unit = {
    entity.playSound(LibSounds.ENEMY_POWER, 1F, 1F)
    val center            = new Vector3(entity.posX, entity.posY + (entity.height / 2), entity.posZ)
    val offsetPos         = center.offset(Vector3.randomVector, offset)
    val directionToCenter = Vector3.directionToPos(offsetPos, center).asInstanceOf[Vector3].divide(divSpeed)
    for (_ <- 0 until amount) {
      ParticleUtil.spawnParticleGlow(
        entity.world,
        offsetPos,
        directionToCenter,
        r,
        g,
        b,
        3F,
        lifetime,
        GlowTexture.MOTE
      )
    }
  }
  def createChargeSpherePacket(
      packetCenter: Vector3,
      entity: Entity,
      amount: Int,
      offset: Double,
      divSpeed: Double,
      r: Float,
      g: Float,
      b: Float,
      lifetime: Int
  ): Unit =
    DanCorePacketHandler.sendToAllAround(
      new ChargeSpherePacket(entity, amount, offset, divSpeed, r, g, b, lifetime),
      TargetPoint.around(entity.dimension, packetCenter, 32)
    )
}
