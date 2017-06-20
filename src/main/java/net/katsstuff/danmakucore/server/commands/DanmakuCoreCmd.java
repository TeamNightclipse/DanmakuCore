/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.server.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class DanmakuCoreCmd extends CommandTreeBase {

	public DanmakuCoreCmd() {
		addSubcommand(new DanLivesCmd());
		addSubcommand(new DanBombsCmd());
		addSubcommand(new DanPowerCmd());
		addSubcommand(new DanScoreCmd());
	}

	@Override
	public String getName() {
		return "danmakucore";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.danmakucore.usage";
	}
}
