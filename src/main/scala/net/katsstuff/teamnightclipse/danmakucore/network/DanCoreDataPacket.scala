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

import java.util.UUID

import io.netty.buffer.ByteBuf
import net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata.{BoundlessDanmakuCoreData, IDanmakuCoreData}
import net.katsstuff.teamnightclipse.danmakucore.helper.NBTHelper
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.TouhouHelper
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.entity.Entity

case class DanCoreDataPacket(data: IDanmakuCoreData, target: UUID) {

  def this(data: IDanmakuCoreData, entity: Entity) = this(data, entity.getUniqueID)
}
object DanCoreDataPacket {

  implicit val converter: MessageConverter[DanCoreDataPacket] = new MessageConverter[DanCoreDataPacket] {

    override def writeBytes(a: DanCoreDataPacket, buf: ByteBuf): Unit = {
      buf.writeFloat(a.data.getPower)
      buf.writeInt(a.data.getScore)
      buf.writeInt(a.data.getLives)
      buf.writeInt(a.data.getBombs)

      buf.writeLong(a.target.getMostSignificantBits)
      buf.writeLong(a.target.getLeastSignificantBits)
    }

    override def readBytes(buf: ByteBuf): DanCoreDataPacket = {
      val data = new BoundlessDanmakuCoreData
      data.setPower(buf.readFloat)
      data.setScore(buf.readInt)
      data.setLives(buf.readInt)
      data.setBombs(buf.readInt)

      val target = new UUID(buf.readLong, buf.readLong)
      DanCoreDataPacket(data, target)
    }
  }

  implicit val handler: ClientMessageHandler[DanCoreDataPacket, Unit] =
    new ClientMessageHandler[DanCoreDataPacket, Unit] {
      override def handle(netHandler: NetHandlerPlayClient, a: DanCoreDataPacket): None.type = {
        scheduler.addScheduledTask(DanCoreDataPacketRunnable(netHandler, a))
        None
      }
    }
}
case class DanCoreDataPacketRunnable(client: NetHandlerPlayClient, packet: DanCoreDataPacket) extends Runnable {
  override def run(): Unit = {
    var entityTarget: Entity = Minecraft.getMinecraft.world.getPlayerEntityByUUID(packet.target)
    if (entityTarget == null)
      entityTarget = NBTHelper.getEntityByUUID(packet.target, Minecraft.getMinecraft.player.world).orElse(null)

    if (entityTarget != null) {
      TouhouHelper.getDanmakuCoreData(entityTarget).foreach { data =>
        data.setPower(packet.data.getPower)
        data.setScore(packet.data.getScore)
        data.setLives(packet.data.getLives)
        data.setBombs(packet.data.getBombs)
      }
    }
  }
}
