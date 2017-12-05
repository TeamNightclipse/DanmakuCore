/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability.danmakuhit

import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.minecraft.entity.Entity
import net.minecraft.util.DamageSource

/**
  * A capability that lets a entity overwrite what happens when it's hit by a danmaku.
  */
trait DanmakuHitBehavior {

  def onHit(danmaku: DanmakuState, hitEntity: Entity, damage: Float, source: DamageSource): Unit
}