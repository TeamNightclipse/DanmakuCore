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
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuChanges
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.network.NetHandlerPlayClient

case class DanmakuUpdatePacket(changes: Seq[DanmakuChanges])
object DanmakuUpdatePacket {

  implicit val converter: MessageConverter[DanmakuUpdatePacket] =
    MessageConverter[Seq[DanmakuChanges]].modify(DanmakuUpdatePacket.apply)(_.changes)

  implicit val handler: ClientMessageHandler[DanmakuUpdatePacket, Unit] =
    new ClientMessageHandler[DanmakuUpdatePacket, Unit] {
      override def handle(netHandler: NetHandlerPlayClient, a: DanmakuUpdatePacket): Option[Unit] = {
        scheduler.addScheduledTask(DanmakuUpdateRunnable(a.changes))
        None
      }
    }
}
case class DanmakuUpdateRunnable(changes: Seq[DanmakuChanges]) extends Runnable {
  override def run(): Unit = changes.foreach(DanmakuCore.proxy.updateDanmakuClient)
}
