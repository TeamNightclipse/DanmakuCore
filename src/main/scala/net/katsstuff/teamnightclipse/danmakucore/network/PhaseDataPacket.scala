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
import net.katsstuff.teamnightclipse.danmakucore.entity.living.EntityDanmakuMob
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound

case class PhaseDataPacket(entityId: Int, phaseTag: NBTTagCompound) {

  def this(entity: Entity, phaseTag: NBTTagCompound) = this(entity.getEntityId, phaseTag)
}
object PhaseDataPacket {

  implicit val converter: MessageConverter[PhaseDataPacket] = MessageConverter.mkDeriver[PhaseDataPacket].apply

  implicit val handler: ClientMessageHandler[PhaseDataPacket, Unit] = new ClientMessageHandler[PhaseDataPacket, Unit] {
    override def handle(netHandler: NetHandlerPlayClient, a: PhaseDataPacket): Option[Unit] = {
      scheduler.addScheduledTask(PhaseDataPacketRunnable(netHandler, a))
      None
    }
  }
}

case class PhaseDataPacketRunnable(client: NetHandlerPlayClient, packet: PhaseDataPacket) extends Runnable {
  override def run(): Unit = {
    val entity = Minecraft.getMinecraft.world.getEntityByID(packet.entityId)
    entity match {
      case danMob: EntityDanmakuMob if packet.phaseTag != null => danMob.getPhaseManager.deserializeNBT(packet.phaseTag)
      case _                                                   =>
    }
  }
}
