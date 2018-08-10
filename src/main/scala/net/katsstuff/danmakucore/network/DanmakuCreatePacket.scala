/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.danmaku.DanmakuState
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
