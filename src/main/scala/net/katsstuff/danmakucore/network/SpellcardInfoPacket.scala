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

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.mirror.network.scalachannel.{ClientMessageHandler, Discriminator, MessageConverter}
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.util.text.ITextComponent

sealed trait SpellcardInfoPacket {
  def uuid: UUID
}
case class AddSpellcardInfo(uuid: UUID, name: ITextComponent, mirror: Boolean, posX: Float, posY: Float) extends SpellcardInfoPacket
object AddSpellcardInfo {
  implicit val disc: Discriminator[AddSpellcardInfo] = Discriminator(0.toByte)
}

case class SetSpellcardInfoName(uuid: UUID, name: ITextComponent) extends SpellcardInfoPacket
object SetSpellcardInfoName {
  implicit val disc: Discriminator[SetSpellcardInfoName] = Discriminator(1.toByte)
}

case class SetSpellcardInfoMirror(uuid: UUID, mirror: Boolean) extends SpellcardInfoPacket
object SetSpellcardInfoMirror {
  implicit val disc: Discriminator[SetSpellcardInfoMirror] = Discriminator(2.toByte)
}

case class SetSpellcardInfoPos(uuid: UUID, posX: Float, posY: Float, absolute: Boolean) extends SpellcardInfoPacket
object SetSpellcardInfoPos {
  implicit val disc: Discriminator[SetSpellcardInfoPos] = Discriminator(3.toByte)
}

case class RemoveSpellcardInfo(uuid: UUID) extends SpellcardInfoPacket
object RemoveSpellcardInfo {
  implicit val disc: Discriminator[RemoveSpellcardInfo] = Discriminator(4.toByte)
}
object SpellcardInfoPacket {

  implicit val converter: MessageConverter[SpellcardInfoPacket] = MessageConverter.mkDeriver[SpellcardInfoPacket].apply

  implicit val handler: ClientMessageHandler[SpellcardInfoPacket, Unit] =
    new ClientMessageHandler[SpellcardInfoPacket, Unit] {
      override def handle(netHandler: NetHandlerPlayClient, a: SpellcardInfoPacket): Option[Unit] = {
        scheduler.addScheduledTask(() => DanmakuCore.proxy.handleSpellcardInfo(a))
        None
      }
    }
}
