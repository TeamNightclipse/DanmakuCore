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
