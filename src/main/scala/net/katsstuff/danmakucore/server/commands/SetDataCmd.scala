/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.server.commands

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
