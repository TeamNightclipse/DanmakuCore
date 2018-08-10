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
import net.katsstuff.danmakucore.danmaku.DanmakuChanges
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
