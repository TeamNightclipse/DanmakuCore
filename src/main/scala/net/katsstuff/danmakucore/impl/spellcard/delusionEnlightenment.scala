/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.spellcard

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate
import net.katsstuff.danmakucore.entity.living.TouhouCharacter
import net.katsstuff.danmakucore.entity.spellcard.{EntitySpellcard, Spellcard, SpellcardEntity}
import net.katsstuff.danmakucore.lib.data.LibShotData
import net.katsstuff.danmakucore.lib.{LibColor, LibSounds, LibSpellcardName}
import net.katsstuff.danmakucore.scalastuff.{DanmakuCreationHelper, DanmakuHelper}
import net.minecraft.entity.EntityLivingBase

private[danmakucore] class SpellcardDelusionEnlightenment
    extends Spellcard(LibSpellcardName.DELUSION_OF_ENLIGHTENMENT) {
  override def instantiate(card: EntitySpellcard, target: Option[EntityLivingBase]) =
    new SpellcardEntityDelusionEnlightenment(this, card, target)
  override def level      = 1
  override def removeTime = 50
  override def endTime    = 140
  override def touhouUser: TouhouCharacter = TouhouCharacter.YOUMU_KONPAKU
}

private[spellcard] class SpellcardEntityDelusionEnlightenment(
    spellcard: SpellcardDelusionEnlightenment,
    card: EntitySpellcard,
    target: Option[EntityLivingBase]
) extends SpellcardEntity(spellcard, card, target) {

  override def onSpellcardUpdate(): Unit = if (!world.isRemote) {
    val danmakuLevelMultiplier = danmakuLevel.getMultiplier

    if(time == 1) {
      DanmakuHelper.playSoundAt(world, posUser, LibSounds.ENEMY_POWER, 0.2F, 1F)
    }

    if(time > 40) {
      for (_ <- 0 until danmakuLevelMultiplier * 2) {
        spawnGroundDanmaku()
      }

      val time40 = time % 40
      if(time40 == 10) {
        DanmakuHelper.playSoundAt(world, posUser, LibSounds.SHOT1, 0.2F, 1F)
      }

      if (time40 < 10) {
        val place = Math.max(0, 10 - time40)
        val danmaku = DanmakuTemplate.builder
          .setUser(user)
          .setSource(card)
          .setMovementData(1D / (place + 1))
          .setShot(LibShotData.SHOT_MEDIUM.setColor(LibColor.COLOR_SATURATED_RED).setDelay(place))
          .build

        DanmakuCreationHelper.createWideShot(danmaku, danmakuLevelMultiplier * 2, 120F, 180F, 1.25D)
      }

      if (time40 == 1) {
        for (i <- 1 to 10) {
          val danmaku = DanmakuTemplate.builder
            .setUser(user)
            .setSource(card)
            .setMovementData(1D / i)
            .setShot(LibShotData.SHOT_MEDIUM.setColor(LibColor.COLOR_SATURATED_RED))
            .build

          DanmakuCreationHelper.createWideShot(danmaku, danmakuLevelMultiplier, 30F, 0F, 0.5D)
        }
      }
    }
  }

  private def spawnGroundDanmaku(): Unit = {
    val direction = Vector3.getVecWithoutY(Vector3.randomVector)

    val posSource = posUser.offset(direction, rng.nextDouble * 24)
    val posReach  = posSource.offset(Vector3.Down, 24)

    val ray = world.rayTraceBlocks(posSource.toVec3d, posReach.toVec3d)

    val spawnPos = if (ray != null) new Vector3(ray.hitVec) else posReach //Can I multiply the vectors here?
    DanmakuHelper.playSoundAt(world, spawnPos, LibSounds.SHOT1, 0.2F, 1F)

    val danmaku = DanmakuTemplate.builder
      .setUser(user)
      .setSource(card)
      .setDirection(Vector3.Up)
      .setMovementData(0.2D)
      .setPos(spawnPos + Vector3.Up)
      .setShot(LibShotData.SHOT_RICE.setColor(LibColor.COLOR_SATURATED_BLUE))
      .build
      .asEntity

    DanmakuCore.proxy.spawnDanmaku(danmaku)
  }
}
