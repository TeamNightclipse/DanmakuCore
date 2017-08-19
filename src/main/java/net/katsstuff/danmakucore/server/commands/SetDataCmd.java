/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.server.commands;

import java.util.function.BiConsumer;

import net.katsstuff.danmakucore.misc.ThrowFunction;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class SetDataCmd <T> extends CommandBase {

	private final String name;
	private final ThrowFunction<String, T, CommandException> parse;
	private final BiConsumer<EntityPlayer, T> process;

	protected SetDataCmd(String name, ThrowFunction<String, T, CommandException> parse, BiConsumer<EntityPlayer, T> process) {
		this.name = name;
		this.parse = parse;
		this.process = process;
	}

	@Override
	public String getName() {
		return name;
	}

	public String translationKey() {
		return "commands.danmakucore." + getName();
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return translationKey() + ".usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length <= 0) {
			throw new WrongUsageException(getUsage(sender));
		}
		else {

			String arg = args[0];
			EntityPlayer player = args.length > 1 ? getPlayer(server, sender, args[1]) : getCommandSenderAsPlayer(sender);
			T res = parse.apply(arg);
			process.accept(player, res);
			notifyCommandListener(player, this, translationKey() + ".success", res, player.getName());
		}
	}
}
