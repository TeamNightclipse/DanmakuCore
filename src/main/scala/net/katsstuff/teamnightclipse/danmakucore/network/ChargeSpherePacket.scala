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
