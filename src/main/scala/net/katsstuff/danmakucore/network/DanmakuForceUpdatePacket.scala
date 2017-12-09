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
import net.katsstuff.danmakucore.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.network.NetHandlerPlayClient

case class DanmakuForceUpdatePacket(state: DanmakuState)
object DanmakuForceUpdatePacket {

  implicit val converter: MessageConverter[DanmakuForceUpdatePacket] =
    MessageConverter[DanmakuState].modify(DanmakuForceUpdatePacket.apply)(_.state)

  implicit val handler: ClientMessageHandler[DanmakuForceUpdatePacket, Unit] =
    new ClientMessageHandler[DanmakuForceUpdatePacket, Unit] {
      override def handle(netHandler: NetHandlerPlayClient, a: DanmakuForceUpdatePacket): Option[Unit] = {
        scheduler.addScheduledTask(DanmakuForceUpdateRunnable(a.state))
        None
      }
    }
}
case class DanmakuForceUpdateRunnable(state: DanmakuState) extends Runnable {
  override def run(): Unit = DanmakuCore.proxy.forceUpdateDanmakuClient(state)
}
