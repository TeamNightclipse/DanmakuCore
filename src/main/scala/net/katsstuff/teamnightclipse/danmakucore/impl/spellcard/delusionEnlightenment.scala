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
package net.katsstuff.teamnightclipse.danmakucore.impl.spellcard

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuTemplate}
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.{EntitySpellcard, Spellcard, SpellcardEntity}
import net.katsstuff.teamnightclipse.danmakucore.impl.shape.ShapeWide
import net.katsstuff.teamnightclipse.danmakucore.lib.data.LibShotData
import net.katsstuff.teamnightclipse.danmakucore.lib.{LibColor, LibSounds, LibSpellcardName}
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanmakuHelper
import net.katsstuff.teamnightclipse.danmakucore.entity.living.TouhouCharacter
import net.minecraft.entity.EntityLivingBase

private[danmakucore] class SpellcardDelusionEnlightenment
    extends Spellcard(LibSpellcardName.DELUSION_OF_ENLIGHTENMENT) {
  override def instantiate(card: EntitySpellcard, target: Option[EntityLivingBase]) =
    new SpellcardEntityDelusionEnlightenment(this, card, target)
  override def level                       = 1
  override def removeTime                  = 50
  override def endTime                     = 140
  override def touhouUser: TouhouCharacter = TouhouCharacter.YOUMU_KONPAKU
}

private[spellcard] class SpellcardEntityDelusionEnlightenment(
    spellcard: SpellcardDelusionEnlightenment,
    card: EntitySpellcard,
    target: Option[EntityLivingBase]
) extends SpellcardEntity(spellcard, card, target) {

  override def onSpellcardUpdate(): Unit = if (!world.isRemote) {
    val danmakuLevelMultiplier = danmakuLevel.getMultiplier

    if (time == 1) {
      DanmakuHelper.playSoundAt(world, posUser, LibSounds.ENEMY_POWER, 0.2F, 1F)
    }

    if (time > 40) {
      val toSpawnGround = for (_ <- 0 until danmakuLevelMultiplier * 2) yield {
        spawnGroundDanmaku()
      }

      val time40 = time % 40
      if (time40 == 10) {
        DanmakuHelper.playSoundAt(world, posUser, LibSounds.SHOT1, 0.2F, 1F)
      }

      val toSpawnTime40Warmup = if (time40 < 10) {
        val place = Math.max(0, 10 - time40)
        val danmaku = DanmakuTemplate.builder
          .setUser(user)
          .setSource(card)
          .setMovementData(1D / (place + 1))
          .setShot(LibShotData.SHOT_MEDIUM.setEdgeColor(LibColor.COLOR_SATURATED_RED).setDelay(place))
          .build

        new ShapeWide(danmaku, danmakuLevelMultiplier * 2, 120F, 180F, 1.25D)
          .draw(danmaku.pos, danmaku.orientation, 0)
          .spawnedDanmaku
          .toSeq
      } else Seq.empty[DanmakuState]

      val toSpawnTime40 = if (time40 == 1) {
        val res = for (i <- 1 to 10) yield {
          val danmaku = DanmakuTemplate.builder
            .setUser(user)
            .setSource(card)
            .setMovementData(1D / i)
            .setShot(LibShotData.SHOT_MEDIUM.setEdgeColor(LibColor.COLOR_SATURATED_RED))
            .build

          new ShapeWide(danmaku, danmakuLevelMultiplier, 30F, 0F, 0.5D)
            .draw(danmaku.pos, danmaku.orientation, 0)
            .spawnedDanmaku
            .toSeq
        }

        res.flatten
      } else Seq.empty[DanmakuState]

      DanmakuCore.proxy.spawnDanmaku(toSpawnGround ++ toSpawnTime40Warmup ++ toSpawnTime40)
    }
  }

  private def spawnGroundDanmaku(): DanmakuState = {
    val direction = Vector3.getVecWithoutY(Vector3.randomVector)

    val posSource = posUser.offset(direction, rng.nextDouble * 24)
    val posReach  = posSource.offset(Vector3.Down, 24)

    val ray = world.rayTraceBlocks(posSource.toVec3d, posReach.toVec3d)

    val spawnPos = if (ray != null) new Vector3(ray.hitVec) else posReach //Can I multiply the vectors here?
    DanmakuHelper.playSoundAt(world, spawnPos, LibSounds.SHOT1, 0.2F, 1F)

    DanmakuTemplate.builder
      .setUser(user)
      .setSource(card)
      .setDirection(Vector3.Up)
      .setMovementData(0.2D)
      .setPos(spawnPos + Vector3.Up)
      .setShot(LibShotData.SHOT_RICE.setEdgeColor(LibColor.COLOR_SATURATED_BLUE))
      .build
      .asEntity
  }
}
