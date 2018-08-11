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
package net.katsstuff.teamnightclipse.danmakucore.server.commands

import net.minecraft.command.{CommandBase, ICommand, ICommandSender, WrongUsageException}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

private[danmakucore] case class SetDataCmd[T](name: String, parse: String => T, process: (EntityPlayer, T) => Unit)
    extends CommandBase {
  override def getName: String = name

  def translationKey: String = "commands.danmakucore." + name

  override def getUsage(sender: ICommandSender): String = translationKey + ".usage"

  override def getRequiredPermissionLevel = 2

  override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit =
    if (args.length <= 0) throw new WrongUsageException(getUsage(sender))
    else {
      import CommandBase._
      val arg    = args(0)
      val player = if (args.length > 1) getPlayer(server, sender, args(1)) else getCommandSenderAsPlayer(sender)
      val res    = parse.apply(arg)

      process(player, res)
      notifyCommandListener(
        player: ICommandSender,
        this: ICommand,
        s"$translationKey.success",
        res.toString,
        player.getName
      )
    }
}
