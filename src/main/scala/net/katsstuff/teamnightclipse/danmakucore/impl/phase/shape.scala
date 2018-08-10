/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.phase

import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{Phase, PhaseType}
import net.katsstuff.teamnightclipse.danmakucore.shape.Shape
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{Phase, PhaseManager, PhaseType}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}

object PhaseTypeShape {
  //noinspection ConvertExpressionToSAM
  def create(shape: Shape, continuous: Boolean): PhaseType = new PhaseType() {
    override def instantiate(phaseManager: PhaseManager): Phase =
      new PhaseShape(phaseManager, this, shape, continuous)
  }
}

private class PhaseShape(manager: PhaseManager, val phaseType: PhaseType, shape: Shape, continuous: Boolean)
    extends Phase(manager) {

  override def init(): Unit = {
    super.init()
    if (continuous) interval = 99999
    else interval = 0
  }

  override def serverUpdate(): Unit = {
    super.serverUpdate()
    if (isCounterStart) {
      val danmakuMob = getEntity
      val res        = shape.draw(new Vector3(danmakuMob), Quat.orientationOf(danmakuMob), _counter)
      if (continuous && res.isDone) _counter = 0
    }
  }
}
