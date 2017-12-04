/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network

import net.katsstuff.danmakucore.entity.living.EntityDanmakuMob
import net.katsstuff.danmakucore.network.scalachannel.{ClientMessageHandler, MessageConverter}
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
