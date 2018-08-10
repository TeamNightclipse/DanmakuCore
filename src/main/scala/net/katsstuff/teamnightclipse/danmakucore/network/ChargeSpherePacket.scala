/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.network

import net.katsstuff.teamnightclipse.mirror.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.TouhouHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.entity.Entity

case class ChargeSpherePacket(
    entity: Int,
    amount: Int,
    offset: Double,
    divSpeed: Double,
    r: Float,
    g: Float,
    b: Float,
    lifetime: Int
) {

  def this(entity: Entity, amount: Int, offset: Double, divSpeed: Double, r: Float, g: Float, b: Float, lifetime: Int) =
    this(entity.getEntityId, amount, offset, divSpeed, r, g, b, lifetime)
}
object ChargeSpherePacket {

  implicit val converter: MessageConverter[ChargeSpherePacket] = MessageConverter.mkDeriver[ChargeSpherePacket].apply

  implicit val handler: ClientMessageHandler[ChargeSpherePacket, Unit] =
    new ClientMessageHandler[ChargeSpherePacket, Unit] {
      override def handle(netHandler: NetHandlerPlayClient, a: ChargeSpherePacket): Option[Unit] = {
        scheduler.addScheduledTask(ChargeSpherePacketRunnable(netHandler, a))
        None
      }
    }
}

case class ChargeSpherePacketRunnable(client: NetHandlerPlayClient, packet: ChargeSpherePacket) extends Runnable {
  override def run(): Unit = {
    val entity = Minecraft.getMinecraft.world.getEntityByID(packet.entity)
    if (entity != null)
      TouhouHelper
        .createChargeSphere(
          entity,
          packet.amount,
          packet.offset,
          packet.divSpeed,
          packet.r,
          packet.g,
          packet.b,
          packet.lifetime
        )
  }
}
