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
import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.network.NetHandlerPlayClient

case class DanmakuCreatePacket(data: Seq[DanmakuState])
object DanmakuCreatePacket {

  implicit val converter: MessageConverter[DanmakuCreatePacket] =
    MessageConverter[Seq[DanmakuState]].modify(DanmakuCreatePacket.apply)(_.data)

  implicit val handler: ClientMessageHandler[DanmakuCreatePacket, Unit] =
    new ClientMessageHandler[DanmakuCreatePacket, Unit] {
      override def handle(netHandler: NetHandlerPlayClient, a: DanmakuCreatePacket): Option[Unit] = {
        scheduler.addScheduledTask(DanmakuCreateRunnable(a.data))
        None
      }
    }
}
case class DanmakuCreateRunnable(states: Seq[DanmakuState]) extends Runnable {
  override def run(): Unit = DanmakuCore.proxy.spawnDanmakuClient(states)
}
