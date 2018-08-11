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

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.mirror.network.scalachannel.{ClientMessageHandler, Discriminator, MessageConverter}
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.util.text.ITextComponent

sealed trait SpellcardInfoPacket {
  def uuid: UUID
}
case class AddSpellcardInfo(uuid: UUID, name: ITextComponent, mirror: Boolean, posX: Float, posY: Float)
    extends SpellcardInfoPacket
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
