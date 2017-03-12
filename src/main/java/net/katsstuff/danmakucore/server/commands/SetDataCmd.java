/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.server.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public abstract class SetDataCmd extends CommandBase {

	public String translationKey() {
		return "commands.danmakucore." + getCommandName();
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return translationKey() + ".usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length <= 0) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		else {

			String arg = args[0];
			EntityPlayer entityplayer = args.length > 1 ? getPlayer(server, sender, args[1]) : getCommandSenderAsPlayer(sender);
			changeData(arg, entityplayer);
		}
	}

	public abstract void changeData(String arg, EntityPlayer player) throws CommandException;
}
