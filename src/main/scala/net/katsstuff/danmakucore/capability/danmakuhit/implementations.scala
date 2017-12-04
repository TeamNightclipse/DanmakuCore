/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.capability.danmakuhit
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.minecraft.entity.Entity
import net.minecraft.util.DamageSource

object AllyDanmakuHitBehavior extends DanmakuHitBehavior {
  def instance: AllyDanmakuHitBehavior.type = this

  override def onHit(danmaku: EntityDanmaku, hitEntity: Entity, damage: Float, source: DamageSource): Unit = {
    val cap =
      danmaku.user.flatMap(user => Option(user.getCapability(CapabilityDanmakuHitBehaviorJ.HIT_BEHAVIOR, null))).collect {
        case AllyDanmakuHitBehavior => ()
      }

    cap match {
      case Some(_) => //NO-OP
      case None    => hitEntity.attackEntityFrom(source, damage)
    }
  }
}
object DefaultHitBehavior extends DanmakuHitBehavior {
  def instance: DefaultHitBehavior.type = this

  override def onHit(danmaku: EntityDanmaku, hitEntity: Entity, damage: Float, source: DamageSource): Unit =
    hitEntity.attackEntityFrom(source, damage)
}
object IgnoreHitBehavior extends DanmakuHitBehavior {
  def instance: IgnoreHitBehavior.type = this

  override def onHit(danmaku: EntityDanmaku, hitEntity: Entity, damage: Float, source: DamageSource): Unit = ()
}
