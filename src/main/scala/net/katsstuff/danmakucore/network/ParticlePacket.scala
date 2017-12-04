/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.network

import net.katsstuff.danmakucore.client.particle.{GlowTexture, ParticleUtil}
import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.network.scalachannel.{ClientMessageHandler, MessageConverter}
import net.minecraft.client.Minecraft
import net.minecraft.client.network.NetHandlerPlayClient

case class ParticlePacket(
    pos: Vector3,
    motion: Vector3,
    r: Float,
    g: Float,
    b: Float,
    scale: Float,
    lifetime: Int,
    texture: GlowTexture
)
object ParticlePacket {

  implicit val converter: MessageConverter[ParticlePacket] = MessageConverter.mkDeriver[ParticlePacket].apply

  implicit val handler: ClientMessageHandler[ParticlePacket, Unit] = new ClientMessageHandler[ParticlePacket, Unit] {
    override def handle(netHandler: NetHandlerPlayClient, a: ParticlePacket): Option[Unit] = {
      scheduler.addScheduledTask(ParticlePacketRunnable(netHandler, a))
      None
    }
  }
}

case class ParticlePacketRunnable(server: NetHandlerPlayClient, packet: ParticlePacket) extends Runnable {
  override def run(): Unit =
    ParticleUtil.spawnParticleGlow(
      Minecraft.getMinecraft.player.world,
      packet.pos,
      packet.motion,
      packet.r,
      packet.g,
      packet.b,
      packet.scale,
      packet.lifetime,
      packet.texture
    )
}
