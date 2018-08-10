/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
