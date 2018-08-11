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
package net.katsstuff.teamnightclipse.danmakucore.network

import net.katsstuff.teamnightclipse.danmakucore.data.ShotData
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.{DataParameter, DataSerializer}

object ShotDataSerializer extends DataSerializer[ShotData]() {
  override def write(buf: PacketBuffer, shot: ShotData): Unit =
    shot
      .serializeByteBuf(buf)
  override def read(buf: PacketBuffer)              = new ShotData(buf)
  override def createKey(id: Int)                   = new DataParameter[ShotData](id, this)
  override def copyValue(value: ShotData): ShotData = value
}
