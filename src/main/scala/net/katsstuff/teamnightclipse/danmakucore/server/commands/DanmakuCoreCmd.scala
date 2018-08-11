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

import net.katsstuff.teamnightclipse.danmakucore.capability.dancoredata.IDanmakuCoreData
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.TouhouHelper
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.server.command.CommandTreeBase

private[danmakucore] class DanmakuCoreCmd extends CommandTreeBase {

  private def processer[T](f: (IDanmakuCoreData, T) => Unit): (EntityPlayer, T) => Unit =
    (p, i) => TouhouHelper.changeAndSyncPlayerData(d => f(d, i), p)

  val parseInt: String => Int     = (arg: String) => CommandBase.parseInt(arg, 0)
  val parseFloat: String => Float = (arg: String) => CommandBase.parseDouble(arg, 0D, 4D).toFloat

  addSubcommand(SetDataCmd("lives", parseInt, processer[Int](_.setLives(_))))
  addSubcommand(SetDataCmd("bombs", parseInt, processer[Int](_.setBombs(_))))
  addSubcommand(SetDataCmd("power", parseFloat, processer[Float](_.setPower(_))))
  addSubcommand(SetDataCmd("score", parseInt, processer[Int](_.setScore(_))))
  override def getName                          = "danmakucore"
  override def getUsage(sender: ICommandSender) = "commands.danmakucore.usage"
}
