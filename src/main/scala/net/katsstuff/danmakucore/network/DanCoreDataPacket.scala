/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network

import java.util.UUID

import io.netty.buffer.ByteBuf
import net.katsstuff.danmakucore.capability.dancoredata.{BoundlessDanmakuCoreData, IDanmakuCoreData}
import net.katsstuff.danmakucore.helper.NBTHelper
import net.katsstuff.danmakucore.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
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
