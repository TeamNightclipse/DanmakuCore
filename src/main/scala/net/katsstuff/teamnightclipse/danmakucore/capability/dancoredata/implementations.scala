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
package net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata

import net.minecraft.util.math.MathHelper

case class BoundlessDanmakuCoreData(var power: Float, var score: Int, var lives: Int, var bombs: Int)
    extends IDanmakuCoreData {

  def this() {
    this(0F, 0, 0, 0)
  }
}

case class BoundedDanmakuCoreData(
    private var _power: Float,
    var score: Int,
    private var _lives: Int,
    private var _bombs: Int,
    powerBound: Float,
    lifeBombBound: Int
) extends IDanmakuCoreData {

  def this(powerBound: Float, lifeBombBound: Int) {
    this(0F, 0, 0, 0, powerBound, lifeBombBound)
  }

  def this() {
    this(4F, 9)
  }

  override def power: Float = _power

  override def power_=(newPower: Float): Unit = _power = MathHelper.clamp(newPower, 0, powerBound)

  override def lives: Int = _lives

  override def lives_=(newLives: Int): Unit = _lives = MathHelper.clamp(newLives, 0, lifeBombBound)

  override def bombs: Int = _bombs

  override def bombs_=(newBombs: Int): Unit = _bombs = MathHelper.clamp(newBombs, 0, lifeBombBound)
}
