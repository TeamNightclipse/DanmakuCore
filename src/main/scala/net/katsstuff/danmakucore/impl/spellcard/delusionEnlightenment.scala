/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.spellcard

import net.katsstuff.danmakucore.data.{Quat, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate
import net.katsstuff.danmakucore.entity.living.TouhouCharacter
import net.katsstuff.danmakucore.entity.spellcard.{EntitySpellcard, Spellcard, SpellcardEntity}
import net.katsstuff.danmakucore.lib.data.LibShotData
import net.katsstuff.danmakucore.lib.{LibColor, LibSpellcardName}
import net.katsstuff.danmakucore.scalastuff.{DanmakuCreationHelper, DanmakuHelper}
import net.minecraft.entity.EntityLivingBase

private[danmakucore] class SpellcardDelusionEnlightenment extends Spellcard(LibSpellcardName.DELUSION_OF_ENLIGHTENMENT) {
  override def instantiate(card: EntitySpellcard, target: Option[EntityLivingBase]) =
    new SpellcardEntityDelusionEnlightenment(this, card, target)
  override def level      = 1
  override def removeTime = 50
  override def endTime    = 100
  override def touhouUser: TouhouCharacter = TouhouCharacter.YOUMU_KONPAKU
}

private class SpellcardEntityDelusionEnlightenment private (
    spellcard: SpellcardDelusionEnlightenment,
    card: EntitySpellcard,
    target: Option[EntityLivingBase]
) extends SpellcardEntity(spellcard, card, target) {

  override def onSpellcardUpdate(): Unit = if (!world.isRemote) {
    val danmakuLevelMultiplier = danmakuLevel.getMultiplier

    DanmakuHelper.playShotSound(card)
    for (_ <- 0 until danmakuLevelMultiplier) {
      spawnGroundDanmaku()
    }

    val time40 = time % 40
    if (time40 < 10) {
      val place = Math.max(0, 10 - time40)
      val danmaku = DanmakuTemplate.builder
        .setUser(user)
        .setSource(card)
        .setMovementData(1D / (place + 1))
        .setShot(LibShotData.SHOT_MEDIUM.setColor(LibColor.COLOR_SATURATED_RED).setDelay(place))
        .build

      DanmakuHelper.playShotSound(card)
      DanmakuCreationHelper
        .createWideShot(Quat.orientationOf(card), danmaku, danmakuLevelMultiplier * 2, 120F, 180F, 1.25D)
    }

    if (time40 == 0) {
      DanmakuHelper.playShotSound(card)
      for (i <- 0 until 11) {
        val danmaku = DanmakuTemplate.builder
          .setUser(user)
          .setSource(card)
          .setMovementData(1D / i)
          .setShot(LibShotData.SHOT_MEDIUM.setColor(LibColor.COLOR_SATURATED_RED))
          .build

        DanmakuCreationHelper.createWideShot(Quat.orientationOf(card), danmaku, danmakuLevelMultiplier, 30F, 0F, 0.5D)
      }
    }
  }

  private def spawnGroundDanmaku() = {
    val direction = Vector3.getVecWithoutY(Vector3.randomVector)

    val posSource = posUser.offset(direction, rng.nextDouble * 16)
    val posReach  = posSource.offset(Vector3.Down, 16)

    val ray = world.rayTraceBlocks(posSource.toVec3d, posReach.toVec3d)

    val spawnPos = if (ray != null) new Vector3(ray.hitVec) else posReach //Can I multiply the vectors here?

    val danmaku = DanmakuTemplate.builder
      .setUser(user)
      .setSource(card)
      .setDirection(Vector3.Up)
      .setMovementData(0.2D)
      .setPos(spawnPos)
      .setShot(LibShotData.SHOT_RICE.setColor(LibColor.COLOR_SATURATED_BLUE))
      .build
      .asEntity

    world.spawnEntity(danmaku)
  }
}
