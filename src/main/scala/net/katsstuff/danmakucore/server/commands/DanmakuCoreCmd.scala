/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.server.commands

import net.katsstuff.danmakucore.capability.dancoredata.IDanmakuCoreData
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.server.command.CommandTreeBase

private[danmakucore] class DanmakuCoreCmd extends CommandTreeBase {

  private def processer[T](f: (IDanmakuCoreData, T) => Unit): (EntityPlayer, T) => Unit =
    (p, i) => TouhouHelper.changeAndSyncPlayerData(d => f(d, i), p)

  val parseInt:   String => Int   = (arg: String) => CommandBase.parseInt(arg, 0)
  val parseFloat: String => Float = (arg: String) => CommandBase.parseDouble(arg, 0D, 4D).toFloat

  addSubcommand(SetDataCmd("lives", parseInt, processer[Int](_.setLives(_))))
  addSubcommand(SetDataCmd("bombs", parseInt, processer[Int](_.setBombs(_))))
  addSubcommand(SetDataCmd("power", parseFloat, processer[Float](_.setPower(_))))
  addSubcommand(SetDataCmd("score", parseInt, processer[Int](_.setScore(_))))
  override def getName                          = "danmakucore"
  override def getUsage(sender: ICommandSender) = "commands.danmakucore.usage"
}
