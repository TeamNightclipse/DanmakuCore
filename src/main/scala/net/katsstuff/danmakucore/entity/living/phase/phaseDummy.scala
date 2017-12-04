/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.phase

object PhaseTypeDummy extends PhaseType {
  def instance: PhaseTypeDummy.type = this
  override def instantiate(phaseManager: PhaseManager) = new PhaseDummy(phaseManager, this)
}
private[phase] class PhaseDummy(manager: PhaseManager, val phaseType: PhaseTypeDummy.type) extends Phase(manager)
